package com.example.scoreboard

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scoreboard.database.DatabaseConstants
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.database.SessionTagDBService
import com.example.scoreboard.database.TagDBService
import com.example.scoreboard.session.Session
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class SessionTagDBServiceTests {

    private lateinit var sessionTagDBService: SessionTagDBService
    private lateinit var tagDBService: TagDBService
    private lateinit var sessionDBService: SessionDBService
    private lateinit var applicationContext: Context

    @Before
    fun setUp() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        sessionTagDBService = SessionTagDBService(applicationContext)
        tagDBService = TagDBService(applicationContext)
        sessionDBService = SessionDBService(applicationContext)
    }

    @After
    fun tearDown() {
        sessionTagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun addTagToSessionTest() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)

        sessionTagDBService.addTagToSession(tagID, sessionID)
        assertSessionTagCreated(sessionID, tagID)
    }

    @Test
    fun addTagToSessionFailInvalidTagID() {
        val tag = Tag("tag_name", -1)
        tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)

        sessionTagDBService.addTagToSession(-1, sessionID)

        val cursor = sessionTagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertFalse(cursor.moveToFirst())
        }
    }

    @Test
    fun addTagToSessionFailInvalidSessionID() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        sessionDBService.addSession(session)

        sessionTagDBService.addTagToSession(tagID, -1)

        val cursor = sessionTagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertFalse(cursor.moveToFirst())
        }
    }

    @Test
    fun removeTagFromSessionTest() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)

        sessionTagDBService.addTagToSession(tagID, sessionID)
        assertSessionTagCreated(sessionID, tagID)
        sessionTagDBService.removeTagFromSession(tagID, sessionID)

        val cursor = sessionTagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertFalse(cursor.moveToFirst())
        }
    }

    @Test
    fun removeTagFromSessionFailInvalidTagID() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)

        sessionTagDBService.addTagToSession(tagID, sessionID)
        assertSessionTagCreated(sessionID, tagID)
        sessionTagDBService.removeTagFromSession(-1, sessionID)
        assertSessionTagCreated(sessionID, tagID)
    }

    @Test
    fun removeTagFromSessionFailInvalidSessionID() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)

        sessionTagDBService.addTagToSession(tagID, sessionID)
        assertSessionTagCreated(sessionID, tagID)
        sessionTagDBService.removeTagFromSession(tagID, -1)
        assertSessionTagCreated(sessionID, tagID)
    }

    @Test
    fun getSessionIDsForTag() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)
        sessionTagDBService.addTagToSession(tagID, sessionID)

        val sessionIDs = sessionTagDBService.getSessionIDsForTag(tagID)
        assertEquals(1, sessionIDs.size)
        assertEquals(sessionID, sessionIDs[0])
    }

    @Test
    fun getSessionIDsForTagNoSessionsForTag() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)

        val sessionIDs = sessionTagDBService.getSessionIDsForTag(tagID)
        assertEquals(0, sessionIDs.size)
    }

    @Test
    fun getSessionIDsForTagInvalidTagID() {
        val sessionIDs = sessionTagDBService.getSessionIDsForTag(-1)
        assertEquals(0, sessionIDs.size)
    }

    @Test
    fun getTagIDsForSession() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)
        sessionTagDBService.addTagToSession(tagID, sessionID)

        val tagIDs = sessionTagDBService.getTagIDsForSession(sessionID)
        assertEquals(1, tagIDs.size)
        assertEquals(tagID, tagIDs[0])
    }

    @Test
    fun getTagIDsForSessionNoTagsForSession() {
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf())
        val sessionID = sessionDBService.addSession(session)

        val tagIDs = sessionTagDBService.getTagIDsForSession(sessionID)
        assertEquals(0, tagIDs.size)
    }

    @Test
    fun getTagIDsForSessionInvalidSessionID() {
        val tagIDs = sessionTagDBService.getTagIDsForSession(-1)
        assertEquals(0, tagIDs.size)
    }

    @Test
    fun deleteSessionTagsOnSessionDelete() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)
        sessionTagDBService.addTagToSession(tagID, sessionID)

        sessionTagDBService.deleteSessionTagsOnSessionDelete(sessionID)

        val cursor = sessionTagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertFalse(cursor.moveToFirst())
        }
    }

    @Test
    fun deleteSessionTagsOnSessionDeleteFailInvalidID() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)
        sessionTagDBService.addTagToSession(tagID, sessionID)

        sessionTagDBService.deleteSessionTagsOnSessionDelete(-1)

        assertSessionTagCreated(sessionID, tagID)
    }

    @Test
    fun deleteSessionTagsOnTagDelete() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)
        sessionTagDBService.addTagToSession(tagID, sessionID)

        sessionTagDBService.deleteSessionTagsOnTagDelete(tagID)

        val cursor = sessionTagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertFalse(cursor.moveToFirst())
        }
    }

    @Test
    fun deleteSessionTagsOnTagDeleteFailInvalidID() {
        val tag = Tag("tag_name", -1)
        val tagID = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val sessionID = sessionDBService.addSession(session)
        sessionTagDBService.addTagToSession(tagID, sessionID)

        sessionTagDBService.deleteSessionTagsOnTagDelete(-1)

        assertSessionTagCreated(sessionID, tagID)
    }

    private fun assertSessionTagCreated(sessionID: Long, tagID: Long) {
        val cursor = sessionTagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME}",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            val getTagID =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.TAG_ID_COLUMN))
            val getSessionID =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN))
            assertEquals(
                tagID,
                getTagID
            )
            assertEquals(
                sessionID,
                getSessionID
            )
        }
    }
}