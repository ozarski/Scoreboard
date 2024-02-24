package com.ozarskiapps.global

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun setCalendarToDayStart(calendar: Calendar){
    calendar.apply{
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

fun setCalendarToDayEnd(calendar: Calendar){
    calendar.apply{
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}

fun durationInSecondsToDaysAndHoursAndMinutes(duration: Long): String{
    val days = duration / SECONDS_IN_DAY
    val hours = (duration - days * SECONDS_IN_DAY) / SECONDS_IN_HOUR
    val minutes = (duration - days * SECONDS_IN_DAY - hours * SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
    return "${days}d ${hours}h ${minutes}min"
}

fun durationInSecondsToHoursAndMinutes(duration: Long): String{
    val hours = duration / SECONDS_IN_HOUR
    val minutes = (duration - hours * SECONDS_IN_HOUR) / SECONDS_IN_MINUTE
    return "${hours}h ${minutes}min"
}

fun formatDate(calendar: Calendar): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.ROOT).format(calendar.time)
}