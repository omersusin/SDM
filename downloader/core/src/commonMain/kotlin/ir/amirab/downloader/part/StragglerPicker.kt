package ir.amirab.downloader.part

// Picks which in-flight part to split when a new connection frees up.
// Slowest ETA first (straggler mitigation); the engine feeds live speeds in
// a later step — this stays pure so it is unit-testable.
object StragglerPicker {
    data class Candidate(
        val id: Long,
        val remaining: Long,
        val bytesPerSec: Double,
    )

    fun pick(
        candidates: List<Candidate>,
        minRemaining: Long = PartSplitSupport.SAFE_ZONE_SIZE,
    ): Candidate? {
        return candidates
            .filter { it.remaining >= minRemaining }
            .maxByOrNull { it.remaining / it.bytesPerSec.coerceAtLeast(1.0) }
    }
}
