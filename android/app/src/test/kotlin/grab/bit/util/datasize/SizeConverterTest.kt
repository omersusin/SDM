package grab.bit.util.datasize

import kotlin.test.Test
import kotlin.test.assertEquals

class SizeConverterTest {
    @Test
    fun binaryBytesUse1024() {
        val oneMiB = SizeConverter.bytesToSize(1024 * 1024, CommonSizeConvertConfigs.BinaryBytes)
        assertEquals(1.0, oneMiB.value)
        assertEquals("MiB", oneMiB.unit.toString())
    }

    @Test
    fun decimalBytesUse1000() {
        val oneMB = SizeConverter.bytesToSize(1000 * 1000, CommonSizeConvertConfigs.DecimalBytes)
        assertEquals(1.0, oneMB.value)
        assertEquals("MB", oneMB.unit.toString())
    }

    @Test
    fun picksLargestFittingFactor() {
        val size = SizeConverter.bytesToSize(1500, CommonSizeConvertConfigs.DecimalBytes)
        assertEquals("KB", size.unit.toString())
        val small = SizeConverter.bytesToSize(999, CommonSizeConvertConfigs.DecimalBytes)
        assertEquals("B", small.unit.toString())
    }

    @Test
    fun zeroStaysBytes() {
        val size = SizeConverter.bytesToSize(0, CommonSizeConvertConfigs.BinaryBytes)
        assertEquals("B", size.unit.toString())
    }
}
