package grab.bit.downloader

// Traffic modes (High/Low/Snail). Limits are global bytes/sec, 0 = unlimited
// (matches DownloadSettings.globalSpeedLimit convention). The scheduler and
// a quick-switch UI apply these in later steps.
enum class SpeedProfile(val bytesPerSec: Long) {
    HIGH(0),
    LOW(512L * 1024),
    SNAIL(64L * 1024),
}
