package grab.bit.downloader

data class DownloadSettings(
    //can be changed after boot!
    var defaultThreadCount: Int = 8,
    var dynamicPartCreationMode: Boolean = true,
    var useServerLastModifiedTime: Boolean = false,
    var globalSpeedLimit: Long = 0,//unlimited
    var useSparseFileAllocation: Boolean = true,
    // aria2 uses 20M; 2kB created swarms of wasteful range requests.
    var minPartSize: Long = 1024 * 1024,//1MB
    var maxDownloadRetryCount: Int = 0,
    var retryDelayMillis: Long = 3_000L,
    // Per-host concurrent connection cap, 0 = unlimited. Enforced at queue
    // activation: a queued job whose host already has this many active jobs
    // is skipped until a slot frees up.
    var maxConnectionsPerHost: Int = 0,
    // Randomized stagger between queue auto-starts, 0 = off. Each activation
    // waits a random 0..interDownloadDelayMs before starting the next job.
    var interDownloadDelayMs: Int = 0,
    // Applied to OkHttp connect/read/write timeouts at client build time
    // (restart required to take effect).
    var httpTimeoutSeconds: Int = 10,
    // WARNING: this is used in boot so make sure to update it before booting
    // make it val or add a way to reload it properly
    var appendExtensionToIncompleteDownloads: Boolean = false,
)
