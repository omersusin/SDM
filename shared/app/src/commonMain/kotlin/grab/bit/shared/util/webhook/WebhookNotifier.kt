package grab.bit.shared.util.webhook

import grab.bit.downloader.DownloadManagerEvents
import grab.bit.downloader.DownloadManagerMinimalControl
import grab.bit.downloader.downloaditem.IDownloadItem
import grab.bit.util.guardedEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Serializable
data class WebhookPayload(
    val event: String,
    val id: Long,
    val name: String,
    val link: String,
    val error: String? = null,
)

fun interface WebhookSender {
    suspend fun send(url: String, body: String)
}

class OkHttpWebhookSender(
    private val client: OkHttpClient = OkHttpClient(),
) : WebhookSender {
    override suspend fun send(url: String, body: String) = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("webhook returned ${response.code}")
            }
        }
    }
}

fun buildWebhookPayload(event: String, downloadItem: IDownloadItem, error: String?): String {
    return Json.encodeToString(
        WebhookPayload.serializer(),
        WebhookPayload(
            event = event,
            id = downloadItem.id,
            name = downloadItem.name,
            link = downloadItem.link,
            error = error,
        ),
    )
}

// POSTs a JSON event to webhookUrl on job completed/failed. Empty url = disabled.
// Mirrors FailedDownloads boot/collect; failures never touch the download itself.
class WebhookNotifier(
    private val downloadManager: DownloadManagerMinimalControl,
    private val scope: CoroutineScope,
    private val webhookUrl: StateFlow<String>,
    private val sender: WebhookSender = OkHttpWebhookSender(),
) {
    private val booted = guardedEntry()

    fun boot() {
        booted.action {
            scope.launch {
                downloadManager.listOfJobsEvents.collect { onEvent(it) }
            }
        }
    }

    private suspend fun onEvent(event: DownloadManagerEvents) {
        val url = webhookUrl.value.takeIf { it.isNotBlank() } ?: return
        val (kind, error) = when (event) {
            is DownloadManagerEvents.OnJobCompleted -> "completed" to null
            is DownloadManagerEvents.OnJobCanceled -> "failed" to event.e.message
            else -> return
        }
        runCatching {
            sender.send(url, buildWebhookPayload(kind, event.downloadItem, error))
        }
    }
}
