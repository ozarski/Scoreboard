package com.ozarskiapps.scoreboard

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun setCalendarToDayStart(calendar: Calendar){
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
}

fun setCalendarToDayEnd(calendar: Calendar){
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
}

fun durationInSecondsToDaysAndHoursAndMinutes(duration: Long): String{
    val days = duration / (24 * 60 * 60)
    val hours = (duration - days * 24 * 60 * 60) / (60 * 60)
    val minutes = (duration - days * 24 * 60 * 60 - hours * 60 * 60) / 60
    return "${days}d ${hours}h ${minutes}min"
}

fun durationInSecondsToHoursAndMinutes(duration: Long): String{
    val hours = duration / (60 * 60)
    val minutes = (duration - hours * 60 * 60) / 60
    return "${hours}h ${minutes}min"
}

fun formatDate(calendar: Calendar): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    return sdf.format(calendar.time)
}