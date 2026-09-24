package grab.bit.util

enum class VideoQuality {
    AUTO,
    HIGHEST,
    P1080,
    P720,
    P480,
    P360,
}

object VideoQualities {
    val default: VideoQuality get() = VideoQuality.AUTO

    fun maxHeightOf(quality: VideoQuality): Int? {
        return when (quality) {
            VideoQuality.AUTO -> null
            VideoQuality.HIGHEST -> null
            VideoQuality.P1080 -> 1080
            VideoQuality.P720 -> 720
            VideoQuality.P480 -> 480
            VideoQuality.P360 -> 360
        }
    }

    // One-time migration from the old Int field (kept in schema for compat).
    // Exact matches map over; anything else (incl. the 720 default) → HIGHEST,
    // except the untouched default 720 which becomes AUTO.
    fun migrateStoredHeight(storedHeight: Int): VideoQuality {
        return when (storedHeight) {
            1080 -> VideoQuality.P1080
            720 -> VideoQuality.AUTO
            480 -> VideoQuality.P480
            360 -> VideoQuality.P360
            else -> VideoQuality.HIGHEST
        }
    }
}
