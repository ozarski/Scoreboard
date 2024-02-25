package com.example.base.session

import com.example.base.Tag
import com.ozarskiapps.global.setCalendarToDayEnd
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
        val today = Calendar.getInstance().also{
            setCalendarToDayEnd(it)
        }
        if (!date.after(today)) {
            this.date.timeInMillis = date.timeInMillis
        }
    }
}