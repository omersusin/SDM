package ir.amirab.downloader.video

data class YtDlpDownloadRequest(
    val url: String,
    val formatId: String?,
    val subtitleLangs: List<String>,
    val outputTemplate: String,
)

// Pure yt-dlp CLI option builder (Seal recipe). Pairs of option to optional
// value; the android runner only executes.
object YtDlpRequestBuilder {
    fun buildOptions(req: YtDlpDownloadRequest): List<Pair<String, String?>> {
        return buildList {
            req.formatId?.let {
                add("-f" to it)
            }
            if (req.subtitleLangs.isNotEmpty()) {
                add("--write-subs" to null)
                add("--embed-subs" to null)
                add("--sub-langs" to req.subtitleLangs.joinToString(","))
            }
            add("--no-playlist" to null)
            add("-o" to req.outputTemplate)
        }
    }
}
