package com.example.scoreboard

import android.content.Context
import android.provider.BaseColumns
import androidx.test.platform.app.InstrumentationRegistry
import com.ozarskiapps.scoreboard.Tag
import com.ozarskiapps.scoreboard.database.DatabaseConstants
import com.ozarskiapps.scoreboard.database.SessionDBService
import com.ozarskiapps.scoreboard.database.TagDBService
import com.ozarskiapps.scoreboard.session.Session
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class SessionDBServiceTests {

    private lateinit var applicationContext: Context
    private lateinit var sessionDBService: SessionDBService

    @Before
    fun setUp() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        sessionDBService = SessionDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
    }

    @After
    fun tearDown() {
        sessionDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.TEST_DATABASE_NAME)
    }

    @Test
    fun addSessionTest() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())
        val id = sessionDBService.addSession(session)
        val cursor = sessionDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionsTable.TABLE_NAME} " +
                    "WHERE ${BaseColumns._ID} = $id",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(
                0,
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            )
            assertEquals(
                calendar.timeInMillis,
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            )
        }
    }

    @Test
    fun addSessionFailNegativeDuration() {
        val calendar = Calendar.getInstance()
        val session = Session(-1, calendar, 0, mutableListOf())
        assertThrows("Session duration cannot be negative", Exception::class.java) {
            sessionDBService.addSession(session)
        }
    }

    @Test
    fun addSessionFailFutureDate() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val session = Session(0, calendar, 0, mutableListOf())
        assertThrows("Session date cannot be in the future", Exception::class.java) {
            sessionDBService.addSession(session)
        }
    }

    @Test
    fun getSessionDataByIDTest() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())
        val id = sessionDBService.addSession(session)

        val sessionAdded = Session(0, calendar, id, mutableListOf())
        assertSessionAdded(sessionAdded)

        val sessionData = sessionDBService.getSessionDataByID(id)
        assertEquals(id, sessionData?.id)
        assertEquals(0L, sessionData?.duration)
        assertEquals(calendar.timeInMillis, sessionData?.date)
    }

    @Test
    fun getSessionDataByIDFailInvalidID() {
        val sessionData = sessionDBService.getSessionDataByID(-1)
        assertEquals(null, sessionData)
    }

    @Test
    fun getSessionWithTagsByIDTest() {
        val calendar = Calendar.getInstance()
        val tags = mutableListOf<Tag>(Tag("tag1", 1), Tag("tag2", 2))
        tags.forEach {
            TagDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME).addTag(it)
        }

        val session = Session(0, calendar, 0, tags)
        val id = sessionDBService.addSession(session)

        val sessionAdded = Session(0, calendar, id, mutableListOf())
        assertSessionAdded(sessionAdded)

        val sessionData = sessionDBService.getSessionWithTagsByID(id)

        assertEquals(id, sessionData?.id)
        assertEquals(0L, sessionData?.getDuration())
        assertEquals(calendar.timeInMillis, sessionData?.getDate()?.timeInMillis)
        assertEquals(2, sessionData?.tags?.size)
    }

    @Test
    fun getSessionWithTagsTestNoTagsForSession() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())
        val id = sessionDBService.addSession(session)

        val sessionAdded = Session(0, calendar, id, mutableListOf())
        assertSessionAdded(sessionAdded)

        val sessionData = sessionDBService.getSessionWithTagsByID(id)

        assertEquals(id, sessionData?.id)
        assertEquals(0L, sessionData?.getDuration())
        assertEquals(calendar.timeInMillis, sessionData?.getDate()?.timeInMillis)
        assertEquals(0, sessionData?.tags?.size)
    }

    @Test
    fun getSessionWithTagsByIDFailInvalidID() {
        val sessionData = sessionDBService.getSessionWithTagsByID(-1)
        assertEquals(null, sessionData)
    }

    @Test
    fun updateSessionTest() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())
        val id = sessionDBService.addSession(session)

        val sessionAdded = Session(0, calendar, id, mutableListOf())
        assertSessionAdded(sessionAdded)

        val newCalendar = Calendar.getInstance()
        newCalendar.add(Calendar.DAY_OF_MONTH, -1)
        val newSession = Session(1, newCalendar, id, mutableListOf())
        sessionDBService.updateSession(newSession)

        val updated = sessionDBService.getSessionDataByID(id)
        assertEquals(1L, updated?.duration)
        assertEquals(newCalendar.timeInMillis, updated?.date)
    }

    @Test
    fun updateSessionFailInvalidID() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())

        val sessionID = sessionDBService.addSession(session)
        val sessionAdded = Session(0, calendar, sessionID, mutableListOf())
        assertSessionAdded(sessionAdded)

        val id = -1L
        val newCalendar = Calendar.getInstance()
        newCalendar.add(Calendar.DAY_OF_MONTH, -1)
        val newSession = Session(1, newCalendar, id, mutableListOf())
        sessionDBService.updateSession(newSession)

        val updated = sessionDBService.getSessionDataByID(sessionID)
        assertEquals(0L, updated?.duration)
        assertEquals(calendar.timeInMillis, updated?.date)
    }

    @Test
    fun updateSessionFailNegativeDuration() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())

        val sessionID = sessionDBService.addSession(session)
        val sessionAdded = Session(0, calendar, sessionID, mutableListOf())
        assertSessionAdded(sessionAdded)

        val id = -1L
        val newCalendar = Calendar.getInstance()
        newCalendar.add(Calendar.DAY_OF_MONTH, -1)
        val newSession = Session(-1, newCalendar, id, mutableListOf())
        assertThrows("Session duration cannot be negative", Exception::class.java) {
            sessionDBService.updateSession(newSession)
        }
        val updated = sessionDBService.getSessionDataByID(sessionID)
        assertEquals(0L, updated?.duration)
        assertEquals(calendar.timeInMillis, updated?.date)
    }

    @Test
    fun updateSessionFailFutureDate() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())

        val sessionID = sessionDBService.addSession(session)
        val sessionAdded = Session(0, calendar, sessionID, mutableListOf())
        assertSessionAdded(sessionAdded)

        val id = -1L
        val newCalendar = Calendar.getInstance()
        newCalendar.add(Calendar.DAY_OF_MONTH, 1)
        val newSession = Session(1, newCalendar, id, mutableListOf())

        assertThrows("Session date cannot be in the future", Exception::class.java) {
            sessionDBService.updateSession(newSession)
        }

        val updated = sessionDBService.getSessionDataByID(sessionID)
        assertEquals(0L, updated?.duration)
        assertEquals(calendar.timeInMillis, updated?.date)
    }

    @Test
    fun deleteSessionByID() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())
        val id = sessionDBService.addSession(session)
        val sessionAdded = Session(session.getDuration(), calendar, id, mutableListOf())
        assertSessionAdded(sessionAdded)
        sessionDBService.deleteSessionByID(id)
        val deleted = sessionDBService.getSessionDataByID(id)
        assertEquals(null, deleted)
    }

    @Test
    fun deleteSessionByIDFailInvalidID() {
        val calendar = Calendar.getInstance()
        val session = Session(0, calendar, 0, mutableListOf())
        val id = sessionDBService.addSession(session)
        val sessionAdded = Session(session.getDuration(), calendar, id, mutableListOf())
        assertSessionAdded(sessionAdded)
        val invalidID = -1L
        sessionDBService.deleteSessionByID(invalidID)
        val deleted = sessionDBService.getSessionDataByID(id)
        assertNotNull(deleted)
    }

    @Test
    fun getAllSessionsTest() {
        val calendar = Calendar.getInstance()
        val session1 = Session(0, calendar, 0, mutableListOf())
        val session2 = Session(0, calendar, 0, mutableListOf())
        val session3 = Session(0, calendar, 0, mutableListOf())

        val session1ID = sessionDBService.addSession(session1)
        val session2ID = sessionDBService.addSession(session2)
        val session3ID = sessionDBService.addSession(session3)

        val session1Added = Session(session1.getDuration(), calendar, session1ID, mutableListOf())
        val session2Added = Session(session2.getDuration(), calendar, session2ID, mutableListOf())
        val session3Added = Session(session3.getDuration(), calendar, session3ID, mutableListOf())

        assertSessionAdded(session1Added)
        assertSessionAdded(session2Added)
        assertSessionAdded(session3Added)

        val sessions = sessionDBService.getAllSessions()
        assertEquals(3, sessions.size)
    }

    @Test
    fun getAllSessionsTestNoSessions() {
        val sessions = sessionDBService.getAllSessions()
        assertEquals(0, sessions.size)
    }

    @Test
    fun getAllSessionsPagingTest(){
        val calendar = Calendar.getInstance()
        val session1 = Session(0, calendar, 0, mutableListOf())
        val session2 = Session(0, calendar, 0, mutableListOf())
        val session3 = Session(0, calendar, 0, mutableListOf())

        val session1ID = sessionDBService.addSession(session1)
        val session2ID = sessionDBService.addSession(session2)
        val session3ID = sessionDBService.addSession(session3)

        val session1Added = Session(session1.getDuration(), calendar, session1ID, mutableListOf())
        val session2Added = Session(session2.getDuration(), calendar, session2ID, mutableListOf())
        val session3Added = Session(session3.getDuration(), calendar, session3ID, mutableListOf())

        assertSessionAdded(session1Added)
        assertSessionAdded(session2Added)
        assertSessionAdded(session3Added)

        val sessions = sessionDBService.getAllSessions(1, 2)
        assertEquals(2, sessions.size)
        assertEquals(session1ID, sessions[0].id)
        assertEquals(session1.getDuration(), sessions[0].getDuration())
        assertEquals(session1.getDate().timeInMillis, sessions[0].getDate().timeInMillis)
        assertEquals(session2ID, sessions[1].id)
        assertEquals(session2.getDuration(), sessions[1].getDuration())
        assertEquals(session2.getDate().timeInMillis, sessions[1].getDate().timeInMillis)
    }

    @Test
    fun getAllSessionsPagingTestNoSessions(){
        val sessions = sessionDBService.getAllSessions(1, 2)
        assertEquals(0, sessions.size)
    }

    @Test
    fun getSessionsByIDsPagingTest(){
        val calendar = Calendar.getInstance()
        val session1 = Session(0, calendar, 0, mutableListOf())
        val session2 = Session(0, calendar, 0, mutableListOf())
        val session3 = Session(0, calendar, 0, mutableListOf())

        val session1ID = sessionDBService.addSession(session1)
        val session2ID = sessionDBService.addSession(session2)
        val session3ID = sessionDBService.addSession(session3)

        val session1Added = Session(session1.getDuration(), calendar, session1ID, mutableListOf())
        val session2Added = Session(session2.getDuration(), calendar, session2ID, mutableListOf())
        val session3Added = Session(session3.getDuration(), calendar, session3ID, mutableListOf())

        assertSessionAdded(session1Added)
        assertSessionAdded(session2Added)
        assertSessionAdded(session3Added)

        val sessions = sessionDBService.getSessionsByIDs(listOf(session1ID, session2ID), 1, 2)
        assertEquals(2, sessions.size)
        assertEquals(session1ID, sessions[0].id)
        assertEquals(session1.getDuration(), sessions[0].getDuration())
        assertEquals(session1.getDate().timeInMillis, sessions[0].getDate().timeInMillis)
        assertEquals(session2ID, sessions[1].id)
        assertEquals(session2.getDuration(), sessions[1].getDuration())
        assertEquals(session2.getDate().timeInMillis, sessions[1].getDate().timeInMillis)
    }

    @Test
    fun getSessionsByIDsPagingTestNoSessions(){
        val sessions = sessionDBService.getSessionsByIDs(listOf(1, 2), 1, 2)
        assertEquals(0, sessions.size)
    }

    @Test
    fun getSessionsByIDsPagingTestInvalidPaging(){
        val calendar = Calendar.getInstance()
        val session1 = Session(0, calendar, 0, mutableListOf())
        val session2 = Session(0, calendar, 0, mutableListOf())
        val session3 = Session(0, calendar, 0, mutableListOf())

        val session1ID = sessionDBService.addSession(session1)
        val session2ID = sessionDBService.addSession(session2)
        val session3ID = sessionDBService.addSession(session3)

        val session1Added = Session(session1.getDuration(), calendar, session1ID, mutableListOf())
        val session2Added = Session(session2.getDuration(), calendar, session2ID, mutableListOf())
        val session3Added = Session(session3.getDuration(), calendar, session3ID, mutableListOf())

        assertSessionAdded(session1Added)
        assertSessionAdded(session2Added)
        assertSessionAdded(session3Added)

        val sessions = sessionDBService.getSessionsByIDs(listOf(session1ID, session2ID), 3, 2)
        assertEquals(0, sessions.size)
    }


    private fun assertSessionAdded(session: Session) {
        val getSession = sessionDBService.getSessionDataByID(session.id)
        assertNotNull(getSession)
        assertEquals(session.id, getSession?.id)
        assertEquals(session.getDuration(), getSession?.duration)
        assertEquals(session.getDate().timeInMillis, getSession?.date)
    }
}