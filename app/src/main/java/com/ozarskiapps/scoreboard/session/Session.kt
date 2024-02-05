package com.ozarskiapps.scoreboard.session

import com.ozarskiapps.scoreboard.Tag
import com.ozarskiapps.scoreboard.setCalendarToDayEnd
import java.util.Calendar

class Session(
    private var duration: Long,
    private val date: Calendar,
    var id: Long,
    val tags: MutableList<Tag>
) {

    constructor(sessionData: SessionData) : this(
        sessionData.duration,
        Calendar.getInstance().apply { timeInMillis = sessionData.date },
        sessionData.id,
        mutableListOf()
    )

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
        com.ozarskiapps.scoreboard.setCalendarToDayEnd(Calendar.getInstance())
        if (date.timeInMillis <= today.timeInMillis) {
            this.date.timeInMillis = date.timeInMillis
        }
    }
}