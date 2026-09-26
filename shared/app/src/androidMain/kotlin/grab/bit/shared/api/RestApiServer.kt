package grab.bit.shared.api

import grab.bit.downloader.NewDownloadItemProps
import grab.bit.downloader.downloaditem.DownloadStatus
import grab.bit.downloader.downloaditem.EmptyContext
import grab.bit.downloader.downloaditem.http.HttpDownloadItem
import grab.bit.downloader.utils.OnDuplicateStrategy
import grab.bit.shared.storage.appsettings.BaseAppSettingsStorage
import grab.bit.shared.util.DownloadSystem
import grab.bit.shared.util.FilenameFixer
import grab.bit.util.HttpUrlUtils
import grab.bit.util.guardedEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.ServerSocket
import java.net.Socket

@Serializable
data class ApiDownload(
    val id: Long,
    val name: String,
    val link: String,
    val status: String,
)

@Serializable
data class ApiAddRequest(
    val url: String,
)

@Serializable
data class ApiAddResponse(
    val id: Long,
)

@Serializable
data class ApiStatus(
    val activeCount: Long,
    val totalCount: Long,
)

// Minimal loopback-only REST API over a raw ServerSocket (no extra deps;
// com.sun.net.httpserver is absent on Android). Gated by apiEnabled,
// optional Bearer token via apiAuthEnabled/apiAuthKey.
class RestApiServer(
    private val downloadSystem: () -> DownloadSystem,
    private val saveLocation: () -> String,
    private val appSettings: BaseAppSettingsStorage,
    private val scope: CoroutineScope,
) : RestApiBoot {
    private val booted = guardedEntry()
    private var server: ServerSocket? = null
    private val json = Json { ignoreUnknownKeys = true }

    override fun boot() {
        booted.action {
            if (!appSettings.apiEnabled.value) {
                return@action
            }
            scope.launch(Dispatchers.IO) {
                runCatching { serve() }
            }
        }
    }

    override fun stop() {
        runCatching { server?.close() }
        server = null
    }

    private suspend fun serve() {
        val port = appSettings.apiPort.value.takeIf { it in 1..65535 } ?: return
        ServerSocket(port, 0, java.net.InetAddress.getByName("127.0.0.1")).use { srv ->
            server = srv
            while (!srv.isClosed) {
                val socket = runCatching { srv.accept() }.getOrNull() ?: break
                scope.launch(Dispatchers.IO) {
                    runCatching { handle(socket) }
                    runCatching { socket.close() }
                }
            }
        }
    }

    private suspend fun handle(socket: Socket) {
        val reader = socket.getInputStream().bufferedReader()
        val requestLine = reader.readLine() ?: return
        val headerLines = generateSequence { reader.readLine() }.takeWhile { it.isNotEmpty() }.toList()
        val parts = requestLine.split(" ")
        if (parts.size < 2) {
            return
        }
        val method = parts[0]
        val path = parts[1].substringBefore("?")
        if (!authorized(headerLines)) {
            respond(socket, 401, """{"error":"unauthorized"}""")
            return
        }
        when {
            method == "GET" && path == "/downloads" -> {
                val items = withContext(Dispatchers.Default) {
                    downloadSystem().downloadManager.getDownloadList()
                }
                val body = json.encodeToString(
                    kotlinx.serialization.builtins.ListSerializer(ApiDownload.serializer()),
                    items.map { ApiDownload(it.id, it.name, it.link, it.status.name) },
                )
                respond(socket, 200, body)
            }
            method == "GET" && path == "/status" -> {
                val system = downloadSystem()
                val active = withContext(Dispatchers.Default) {
                    system.downloadManager.getActiveCount()
                }
                val total = withContext(Dispatchers.Default) {
                    system.downloadManager.getDownloadList().size
                }
                respond(socket, 200, json.encodeToString(ApiStatus.serializer(), ApiStatus(active.toLong(), total.toLong())))
            }
            method == "POST" && path == "/downloads" -> {
                val length = headerLines.firstOrNull { it.startsWith("Content-Length:", ignoreCase = true) }
                    ?.substringAfter(":")?.trim()?.toIntOrNull() ?: 0
                if (length <= 0 || length > 4096) {
                    respond(socket, 400, """{"error":"bad body"}""")
                    return
                }
                val chars = CharArray(length)
                var read = 0
                while (read < length) {
                    val n = reader.read(chars, read, length - read)
                    if (n <= 0) break
                    read += n
                }
                val url = runCatching {
                    json.decodeFromString(ApiAddRequest.serializer(), String(chars, 0, read)).url
                }.getOrNull()?.trim().orEmpty()
                if (!HttpUrlUtils.isValidUrl(url)) {
                    respond(socket, 400, """{"error":"invalid url"}""")
                    return
                }
                val id = withContext(Dispatchers.Default) {
                    addUrl(url)
                }
                respond(socket, 201, json.encodeToString(ApiAddResponse.serializer(), ApiAddResponse(id)))
            }
            else -> respond(socket, 404, """{"error":"not found"}""")
        }
    }

    private suspend fun addUrl(url: String): Long {
        val system = downloadSystem()
        val folder = saveLocation()
        val name = FilenameFixer.fix(HttpUrlUtils.extractNameFromLink(url) ?: url.substringAfterLast("/"))
        val item = HttpDownloadItem(
            link = url,
            id = -1,
            folder = folder,
            name = name,
            dateAdded = System.currentTimeMillis(),
            status = DownloadStatus.Added,
        )
        return system.addDownload(
            newItemsToAdd = listOf(
                NewDownloadItemProps(
                    downloadItem = item,
                    extraConfig = null,
                    onDuplicateStrategy = OnDuplicateStrategy.AddNumbered,
                    context = EmptyContext,
                )
            ),
        ).firstOrNull() ?: -1
    }

    private fun authorized(headers: List<String>): Boolean {
        if (!appSettings.apiAuthEnabled.value) {
            return true
        }
        val expected = appSettings.apiAuthKey.value
        if (expected.isBlank()) {
            return false
        }
        val header = headers.firstOrNull { it.startsWith("Authorization:", ignoreCase = true) }
            ?.substringAfter(":")?.trim() ?: return false
        return header == "Bearer $expected"
    }

    private fun respond(socket: Socket, code: Int, body: String) {
        val status = when (code) {
            200 -> "OK"
            201 -> "Created"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            404 -> "Not Found"
            else -> "Error"
        }
        val bytes = body.toByteArray()
        val out = socket.getOutputStream()
        out.write("HTTP/1.1 $code $status\r\nContent-Type: application/json\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n".toByteArray())
        out.write(bytes)
        out.flush()
    }
}
