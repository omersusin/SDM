package grab.bit.downloader.part

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SequentialScheduleTest {
    @Test
    fun capsConcurrentAtTwoWhenSequential() {
        assertEquals(2, SequentialSchedule.effectivePartitionCount(8, sequential = true))
    }

    @Test
    fun keepsSingleConnectionWhenSequential() {
        assertEquals(1, SequentialSchedule.effectivePartitionCount(1, sequential = true))
    }

    @Test
    fun passesThroughWhenNotSequential() {
        assertEquals(8, SequentialSchedule.effectivePartitionCount(8, sequential = false))
    }

    @Test
    fun disablesDynamicSplitWhenSequential() {
        assertFalse(SequentialSchedule.allowDynamicSplit(sequential = true, dynamicSetting = true))
    }

    @Test
    fun keepsDynamicSplitWhenNotSequential() {
        assertTrue(SequentialSchedule.allowDynamicSplit(sequential = false, dynamicSetting = true))
        assertFalse(SequentialSchedule.allowDynamicSplit(sequential = false, dynamicSetting = false))
    }
}
