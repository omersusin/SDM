package grab.bit.shared.ui.configurable

import grab.bit.resources.Res
import grab.bit.util.compose.StringSource
import grab.bit.util.compose.asStringSource
import grab.bit.util.compose.combineStringSources
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

fun DayOfWeek.stringSource(): StringSource = when (this) {
    DayOfWeek.MONDAY -> Res.string.monday
    DayOfWeek.TUESDAY -> Res.string.tuesday
    DayOfWeek.WEDNESDAY -> Res.string.wednesday
    DayOfWeek.THURSDAY -> Res.string.thursday
    DayOfWeek.FRIDAY -> Res.string.friday
    DayOfWeek.SATURDAY -> Res.string.saturday
    DayOfWeek.SUNDAY -> Res.string.sunday
}.asStringSource()

fun Set<DayOfWeek>.describeDays(): StringSource =
    sorted().map { it.stringSource() }.combineStringSources(", ")

fun LocalTime.hourMinuteString(): String {
    val hour = hour.toString().padStart(2, '0')
    val min = minute.toString().padStart(2, '0')
    return "$hour:$min"
}

fun enabledDisabledDescribe(value: Boolean): StringSource =
    (if (value) Res.string.enabled else Res.string.disabled).asStringSource()
