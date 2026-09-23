package ir.amirab.downloader.part

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

// Coarse per-part speed signal for straggler splitting. Overall average is
// deliberate: stable, cheap (no hot-loop bookkeeping), and a stalled part's
// rate decays on its own as time passes without bytes.
// ponytail: overall average, not sliding window — refine if slow parts hide
// behind fast starts.
class PartSpeedSampler {
    private var totalBytes = 0L
    private var start: Instant? = null

    fun add(bytes: Long, now: Instant = Clock.System.now()) {
        if (start == null) {
            start = now
        }
        totalBytes += bytes
    }

    fun bytesPerSec(now: Instant = Clock.System.now()): Double {
        val start = start ?: return 0.0
        val elapsedSecs = (now - start).inWholeMilliseconds / 1000.0
        return if (elapsedSecs <= 0) 0.0 else totalBytes / elapsedSecs
    }

    fun reset() {
        totalBytes = 0L
        start = null
    }
}
