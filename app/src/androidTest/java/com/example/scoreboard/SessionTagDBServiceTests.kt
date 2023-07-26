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
        sessionTagDBService = SessionTagDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
        tagDBService = TagDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
        sessionDBService = SessionDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
    }

    @After
    fun tearDown() {
        sessionTagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.TEST_DATABASE_NAME)
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

    @Test
    fun getSessionsForTagIDsTestOneTag(){
        val tag = Tag("tag_name", -1)
        tag.id = tagDBService.addTag(tag)
        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val session2 = Session(0, Calendar.getInstance(), -1, mutableListOf())
        val sessionID = sessionDBService.addSession(session)
        sessionDBService.addSession(session2)

        val sessions = sessionTagDBService.getSessionsForTagIDs(listOf(tag.id))
        assertEquals(1, sessions.size)
        assertEquals(sessionID, sessions[0].id)
        assertEquals(session.getDate(), sessions[0].getDate())
        assertEquals(session.tags.size, sessions[0].tags.size)
        assertEquals(session.getDuration(), sessions[0].getDuration())
    }

    @Test
    fun getSessionsForTagIDsTestTwoTags(){
        val tag = Tag("tag_name", -1)
        tag.id = tagDBService.addTag(tag)
        val tag2 = Tag("tag_name2", -1)
        tag2.id = tagDBService.addTag(tag2)

        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag2))
        val session2 = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))

        val sessionID = sessionDBService.addSession(session)
        val sessionID2 = sessionDBService.addSession(session2)

        val sessionsTag = sessionTagDBService.getSessionsForTagIDs(listOf(tag.id))
        assertEquals(1, sessionsTag.size)
        assertEquals(sessionID2, sessionsTag[0].id)
        assertEquals(session2.getDate(), sessionsTag[0].getDate())
        assertEquals(session2.tags.size, sessionsTag[0].tags.size)
        assertEquals(session2.getDuration(), sessionsTag[0].getDuration())

        val sessionsTag2 = sessionTagDBService.getSessionsForTagIDs(listOf(tag2.id))
        assertEquals(1, sessionsTag2.size)
        assertEquals(sessionID, sessionsTag2[0].id)
        assertEquals(session.getDate(), sessionsTag2[0].getDate())
        assertEquals(session.tags.size, sessionsTag2[0].tags.size)
        assertEquals(session.getDuration(), sessionsTag2[0].getDuration())
    }

    @Test
    fun getSessionsForTagIDsTestTwoTagsOneSession(){
        val tag = Tag("tag_name", -1)
        tag.id = tagDBService.addTag(tag)
        val tag2 = Tag("tag_name2", -1)
        tag2.id = tagDBService.addTag(tag2)

        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag, tag2))
        val sessionID = sessionDBService.addSession(session)
        val sessionsBothTags = sessionTagDBService.getSessionsForTagIDs(listOf(tag.id, tag2.id))
        assertEquals(1, sessionsBothTags.size)
        assertEquals(sessionID, sessionsBothTags[0].id)
        assertEquals(session.getDate(), sessionsBothTags[0].getDate())
        assertEquals(session.tags.size, sessionsBothTags[0].tags.size)
        assertEquals(session.getDuration(), sessionsBothTags[0].getDuration())

        val sessionsOneTag = sessionTagDBService.getSessionsForTagIDs(listOf(tag.id))
        assertEquals(1, sessionsBothTags.size)
        assertEquals(sessionID, sessionsBothTags[0].id)
        assertEquals(session.getDate(), sessionsBothTags[0].getDate())
        assertEquals(session.tags.size, sessionsBothTags[0].tags.size)
        assertEquals(session.getDuration(), sessionsBothTags[0].getDuration())
    }

    @Test
    fun getSessionsForTagIDsTwoTagsTwoSessions(){
        val tag = Tag("tag_name", -1)
        tag.id = tagDBService.addTag(tag)
        val tag2 = Tag("tag_name2", -1)
        tag2.id = tagDBService.addTag(tag2)

        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag))
        val session2 = Session(0, Calendar.getInstance(), -1, mutableListOf(tag, tag2))

        sessionDBService.addSession(session)
        val sessionID2 = sessionDBService.addSession(session2)

        val sessionsBothTags = sessionTagDBService.getSessionsForTagIDs(listOf(tag.id, tag2.id))
        assertEquals(1, sessionsBothTags.size)
        assertEquals(sessionID2, sessionsBothTags[0].id)
        assertEquals(session2.getDate(), sessionsBothTags[0].getDate())
        assertEquals(session2.tags.size, sessionsBothTags[0].tags.size)
        assertEquals(session2.getDuration(), sessionsBothTags[0].getDuration())
    }

    @Test
    fun getSessionsForTagIDsNoSessionsForTag(){
        val tag = Tag("tag_name", -1)
        tag.id = tagDBService.addTag(tag)
        val tag2 = Tag("tag_name2", -1)
        tag2.id = tagDBService.addTag(tag2)

        val session = Session(0, Calendar.getInstance(), -1, mutableListOf(tag2))
        val sessionID = sessionDBService.addSession(session)

        val sessions = sessionTagDBService.getSessionsForTagIDs(listOf(tag.id))
        assertEquals(0, sessions.size)
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