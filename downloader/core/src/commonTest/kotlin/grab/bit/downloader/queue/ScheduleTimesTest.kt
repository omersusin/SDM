package grab.bit.downloader.queue

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ScheduleTimesTest {
    private val weekdays = setOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
    )

    @Test
    fun disabledScheduleIsAlwaysActive() {
        val s = ScheduleTimes.default()
        assertTrue(s.isActiveAt(DayOfWeek.SUNDAY, LocalTime(3, 0)))
    }

    @Test
    fun windowBounds() {
        val s = ScheduleTimes(
            daysOfWeek = weekdays,
            startTime = LocalTime(2, 30),
            endTime = LocalTime(7, 30),
            enabledStartTime = true,
            enabledEndTime = true,
        )
        assertFalse(s.isActiveAt(DayOfWeek.MONDAY, LocalTime(2, 29)))
        assertTrue(s.isActiveAt(DayOfWeek.MONDAY, LocalTime(2, 30)))
        assertTrue(s.isActiveAt(DayOfWeek.MONDAY, LocalTime(7, 29)))
        assertFalse(s.isActiveAt(DayOfWeek.MONDAY, LocalTime(7, 30)))
        assertFalse(s.isActiveAt(DayOfWeek.SATURDAY, LocalTime(5, 0)))
    }

    @Test
    fun overnightWindow() {
        val s = ScheduleTimes(
            daysOfWeek = setOf(DayOfWeek.MONDAY),
            startTime = LocalTime(22, 0),
            endTime = LocalTime(6, 0),
            enabledStartTime = true,
            enabledEndTime = true,
        )
        assertTrue(s.isActiveAt(DayOfWeek.MONDAY, LocalTime(23, 0)))
        assertTrue(s.isActiveAt(DayOfWeek.TUESDAY, LocalTime(5, 0)))
        assertFalse(s.isActiveAt(DayOfWeek.MONDAY, LocalTime(12, 0)))
        assertFalse(s.isActiveAt(DayOfWeek.WEDNESDAY, LocalTime(5, 0)))
    }
}
