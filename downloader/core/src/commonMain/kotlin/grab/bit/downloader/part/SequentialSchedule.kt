package grab.bit.downloader.part

// Pure scheduling rules for sequential (preview-friendly) downloads.
// Kept side-effect free so it is unit-testable; the engine only reads it.
object SequentialSchedule {
    const val MAX_CONCURRENT_SEQUENTIAL = 2

    fun effectivePartitionCount(requested: Int, sequential: Boolean): Int {
        if (!sequential) return requested
        return requested.coerceAtMost(MAX_CONCURRENT_SEQUENTIAL).coerceAtLeast(1)
    }

    fun allowDynamicSplit(sequential: Boolean, dynamicSetting: Boolean): Boolean {
        return dynamicSetting && !sequential
    }
}
