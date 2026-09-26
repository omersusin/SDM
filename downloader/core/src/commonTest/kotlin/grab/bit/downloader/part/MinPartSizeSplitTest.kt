package grab.bit.downloader.part

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MinPartSizeSplitTest {
    private val MB = 1024L * 1024L

    private fun partOf(sizeBytes: Long): RangedPart {
        return RangedPart(from = 0, to = sizeBytes - 1)
    }

    @Test
    fun defaultThresholdKeepsAntiSwarmFloor() {
        // 5MB part splits by default (above 1MB SAFE_ZONE_SIZE)...
        assertTrue(PartSplitSupport(partOf(5 * MB)).canSplit())
        // ...but a tiny part does not.
        assertFalse(PartSplitSupport(partOf(512 * 1024)).canSplit())
    }

    @Test
    fun customMinSplitSizeBlocksSmallDynamicSplits() {
        // 5MB delta is splittable by default but not with a 20MB minPartSize.
        assertFalse(PartSplitSupport(partOf(5 * MB)).canSplit(20 * MB))
        assertNull(PartSplitSupport(partOf(5 * MB)).splitPart(20 * MB))
    }

    @Test
    fun customMinSplitSizeAllowsLargeDynamicSplits() {
        val support = PartSplitSupport(partOf(25 * MB))
        assertTrue(support.canSplit(20 * MB))
        assertNotNull(support.splitPart(20 * MB))
    }

    @Test
    fun pickerMinRemainingFiltersBelowThreshold() {
        val candidates = listOf(
            StragglerPicker.Candidate(id = 1, remaining = 5 * MB, bytesPerSec = 1.0),
        )
        assertNull(StragglerPicker.pick(candidates, minRemaining = 20 * MB))
        assertNotNull(StragglerPicker.pick(candidates))
    }
}
