
package grab.bit.downloader.utils

// Max engine parts/connections per job. Mirrors ThreadCountLimitation in the
// shared UI layer (kept local: downloader core must not depend on UI code).
const val MAX_PART_COUNT = 256

// Resolves how many parts/connections an engine job may use.
// UI layers clamp their input ranges, but persisted rows (DB/API/per-host
// import) carry no invariant: a 0/negative count used to crash splitToRange's
// require() and stall part loops. Clamp once here so every caller degrades
// to single-part instead of throwing.
fun resolvePartCount(preferred: Int?, default: Int): Int {
    return (preferred?.takeIf { it >= 1 } ?: default.takeIf { it >= 1 } ?: 1)
        .coerceIn(1, MAX_PART_COUNT)
}
