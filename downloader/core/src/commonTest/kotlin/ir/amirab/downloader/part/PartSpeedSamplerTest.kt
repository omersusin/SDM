package ir.amirab.downloader.part

import kotlin.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PartSpeedSamplerTest {
    private val t0 = Instant.fromEpochMilliseconds(1_000_000)

    private fun at(millisAfter: Long) = Instant.fromEpochMilliseconds(t0.toEpochMilliseconds() + millisAfter)

    @Test
    fun computesOverallAverage() {
        val sampler = PartSpeedSampler()
        sampler.add(1000, t0)
        sampler.add(1000, at(1000))
        assertEquals(1000.0, sampler.bytesPerSec(at(2000)))
    }

    @Test
    fun stalledRateDecaysWithoutNewBytes() {
        val sampler = PartSpeedSampler()
        sampler.add(2000, t0)
        val early = sampler.bytesPerSec(at(1000))
        val late = sampler.bytesPerSec(at(4000))
        assertTrue(late < early)
        assertEquals(500.0, late)
    }

    @Test
    fun noSamplesGivesZero() {
        assertEquals(0.0, PartSpeedSampler().bytesPerSec(at(5000)))
    }

    @Test
    fun resetClears() {
        val sampler = PartSpeedSampler()
        sampler.add(5000, t0)
        sampler.reset()
        assertEquals(0.0, sampler.bytesPerSec(at(1000)))
        sampler.add(2000, at(2000))
        assertEquals(2000.0, sampler.bytesPerSec(at(3000)))
    }
}
