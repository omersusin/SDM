package ir.amirab.downloader.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class YtDlpFormat(
    val id: String = "",
    val ext: String = "",
    val vcodec: String? = null,
    val acodec: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val filesize: Long? = null,
    @SerialName("filesize_approx")
    val filesizeApprox: Long? = null,
    val url: String = "",
)

@Serializable
data class YtDlpSubtitle(
    val ext: String = "",
    val url: String = "",
    val name: String? = null,
)

@Serializable
data class YtDlpPlaylistEntry(
    val id: String = "",
    val title: String? = null,
    val url: String = "",
)

@Serializable
data class YtDlpPlaylist(
    val id: String = "",
    val title: String? = null,
    val entries: List<YtDlpPlaylistEntry> = emptyList(),
)

@Serializable
data class YtDlpInfo(
    val id: String = "",
    val title: String? = null,
    val formats: List<YtDlpFormat> = emptyList(),
    val subtitles: Map<String, List<YtDlpSubtitle>> = emptyMap(),
)

// Decodes yt-dlp --dump-single-json output into our VideoFormat model.
// Pure Kotlin: the android runner only supplies stdout (Seal recipe).
object YtDlpInfoParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parse(stdout: String): YtDlpInfo {
        return json.decodeFromString(YtDlpInfo.serializer(), stdout)
    }

    fun parsePlaylist(stdout: String): YtDlpPlaylist {
        return json.decodeFromString(YtDlpPlaylist.serializer(), stdout)
    }

    fun playlistUrls(playlist: YtDlpPlaylist): List<String> {
        return playlist.entries.mapNotNull { it.url.takeIf { u -> u.isNotEmpty() } }
    }

    fun subtitleLanguages(info: YtDlpInfo): List<String> {
        return info.subtitles.keys.sorted()
    }

    fun subtitleUrls(info: YtDlpInfo, language: String): List<String> {
        return info.subtitles[language].orEmpty()
            .filter { it.url.isNotEmpty() }
            .map { it.url }
    }

    fun toVideoFormats(info: YtDlpInfo): List<VideoFormat> {
        return info.formats
            .filter { it.id.isNotEmpty() && it.url.isNotEmpty() }
            .map {
                VideoFormat(
                    id = it.id,
                    ext = it.ext,
                    vcodec = it.vcodec,
                    acodec = it.acodec,
                    width = it.width,
                    height = it.height,
                    filesize = it.filesize ?: it.filesizeApprox,
                    url = it.url,
                )
            }
    }
}
