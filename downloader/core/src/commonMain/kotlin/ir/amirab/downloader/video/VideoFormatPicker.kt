package ir.amirab.downloader.video

data class VideoFormat(
    val id: String,
    val ext: String,
    val vcodec: String?,
    val acodec: String?,
    val width: Int?,
    val height: Int?,
    val filesize: Long?,
    val url: String,
) {
    val isAudioOnly: Boolean get() = vcodec == "none" && acodec != null && acodec != "none"
    val isVideoOnly: Boolean get() = acodec == "none" && vcodec != null && vcodec != "none"
    val hasAudioAndVideo: Boolean get() = !isAudioOnly && !isVideoOnly
}

// Pure format-selection slice of video downloading (Seal recipe: split by
// codec, join picked ids with "+"). The yt-dlp runtime comes in a later step.
object VideoFormatPicker {
    fun audioOnly(formats: List<VideoFormat>): List<VideoFormat> {
        return formats.filter { it.isAudioOnly }
    }

    fun videoFormats(formats: List<VideoFormat>): List<VideoFormat> {
        return formats.filter { !it.isAudioOnly }
    }

    fun formatIdString(ids: List<String>): String {
        return ids.joinToString("+")
    }

    fun bestForHeight(formats: List<VideoFormat>, maxHeight: Int): VideoFormat? {
        return formats
            .filter { it.hasAudioAndVideo && (it.height ?: Int.MAX_VALUE) <= maxHeight }
            .maxByOrNull { it.height ?: 0 }
            ?: formats
                .filter { it.hasAudioAndVideo }
                .minByOrNull { it.height ?: Int.MAX_VALUE }
    }
}
