package com.example.scoreboard

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Calendar

class SessionTests {

    @Test
    fun setSessionDurationTest(){
        val session = Session(0, Calendar.getInstance(), 0)
        session.setDuration(3600)
        assertEquals(3600, session.getDuration())
    }

    @Test
    fun setSessionDurationFailNegativeDuration(){
        val session = Session(0, Calendar.getInstance(), 0)
        session.setDuration(-3600)
        assertEquals(0, session.getDuration())
    }

    @Test
    fun setSessionDateTest(){
        val session = Session(0, Calendar.getInstance(), 0)
        val date = Calendar.getInstance()
        session.setDate(date)
        assertEquals(date.timeInMillis, session.getDate().timeInMillis)
    }

    @Test
    fun setSessionDateFailDateFutureDate(){
        val initialDate = Calendar.getInstance()
        val session = Session(0, initialDate, 0)
        val date = Calendar.getInstance()
        date.add(Calendar.DAY_OF_MONTH, 1)
        session.setDate(date)
        assertEquals(initialDate.timeInMillis, session.getDate().timeInMillis)
    }
}