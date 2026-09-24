package grab.bit.downloader.video

import grab.bit.util.VideoQuality
import grab.bit.util.VideoQualities

data class VideoFormat(
    val id: String,
    val ext: String,
    val vcodec: String?,
    val acodec: String?,
    val width: Int?,
    val height: Int?,
    val filesize: Long?,
    val url: String,
    val tbr: Double? = null,
    val fps: Double? = null,
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

    // yt-dlp FormatSorter recipe (worst→best tuple, trimmed): AV-complete
    // first, then resolution, bitrate, size. Nulls sink to the bottom.
    fun sortBestFirst(formats: List<VideoFormat>): List<VideoFormat> {
        return formats.sortedWith(
            compareByDescending<VideoFormat> { it.hasAudioAndVideo }
                .thenByDescending { it.height ?: -1 }
                .thenByDescending { it.tbr ?: -1.0 }
                .thenByDescending { it.filesize ?: -1L }
                .thenBy { it.id }
        )
    }

    fun bestForHeight(formats: List<VideoFormat>, maxHeight: Int): VideoFormat? {
        return formats
            .filter { it.hasAudioAndVideo && (it.height ?: Int.MAX_VALUE) <= maxHeight }
            .maxByOrNull { it.height ?: 0 }
            ?: formats
                .filter { it.hasAudioAndVideo }
                .minByOrNull { it.height ?: Int.MAX_VALUE }
    }

    fun pickForQuality(formats: List<VideoFormat>, quality: VideoQuality): VideoFormat? {
        val videos = videoFormats(formats)
        return when (quality) {
            VideoQuality.AUTO -> sortBestFirst(videos).firstOrNull()
            VideoQuality.HIGHEST -> sortBestFirst(videos).firstOrNull { it.hasAudioAndVideo }
                ?: sortBestFirst(videos).firstOrNull()
            else -> VideoQualities.maxHeightOf(quality)?.let { bestForHeight(videos, it) }
        }
    }
}
