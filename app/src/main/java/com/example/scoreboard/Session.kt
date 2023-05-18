package com.example.scoreboard

import java.util.Calendar

class Session(private var duration: Long, private val date: Calendar, val id: Long) {

    fun getDuration(): Long {
        return duration
    }

    fun getDate(): Calendar {
        return date
    }

    fun setDuration(duration: Long) {
        if (duration >= 0) {
            this.duration = duration
        }
    }

    fun setDate(date: Calendar) {
        val today = Calendar.getInstance()
        setCalendarToDayEnd(Calendar.getInstance())
        if (date.timeInMillis <= today.timeInMillis) {
            this.date.timeInMillis = date.timeInMillis
        }
    }
}