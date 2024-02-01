package com.example.scoreboard

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scoreboard.database.DatabaseConstants
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.database.StatsDBService
import com.example.scoreboard.database.TagDBService
import com.example.scoreboard.session.Session
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class StatsDBServiceTests {

    private lateinit var statsDBService: StatsDBService
    private lateinit var sessionDBService: SessionDBService
    private lateinit var tagDBService: TagDBService
    private lateinit var applicationContext: Context

    @Before
    fun setUp() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        statsDBService = StatsDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
        sessionDBService = SessionDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
        tagDBService = TagDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
    }

    @After
    fun tearDown() {
        statsDBService.close()
        sessionDBService.close()
        tagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.TEST_DATABASE_NAME)
    }

    @Test
    fun getTotalDurationTest(){
        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf())
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf())
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf())
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)
        val duration = statsDBService.getTotalDuration()
        assertEquals(60L, duration)
    }

    @Test
    fun getTotalDurationNoSessionsInDatabase(){
        val duration = statsDBService.getTotalDuration()
        assertEquals(0L, duration)
    }

    @Test
    fun getDurationForTag(){
        val tag1 = createTag("tag1")
        val tag2 = createTag("tag2")
        assert(tag1 != null)
        assert(tag2 != null)

        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf(tag1!!))
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf(tag1, tag2!!))
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf(tag1))
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)
        val durationTag1 = statsDBService.getDurationForTag(tag1.id)
        val durationTag2 = statsDBService.getDurationForTag(tag2.id)
        assertEquals(60L, durationTag1)
        assertEquals(20L, durationTag2)
    }

    @Test
    fun getDurationForTagNoSessionsForTag(){
        val tag1 = createTag("tag1")
        val tag2 = createTag("tag2")
        assert(tag1 != null)
        assert(tag2 != null)

        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf(tag1!!))
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf(tag1))
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)

        val duration = statsDBService.getDurationForTag(tag2!!.id)
        assertEquals(0L, duration)
    }

    @Test
    fun getDurationForTagInvalidTagID(){
        val tag1 = createTag("tag1")
        assert(tag1 != null)

        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf(tag1!!))
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf(tag1))
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)

        val duration = statsDBService.getDurationForTag(-1)
        assertEquals(0L, duration)
    }

    @Test
    fun getAllTagsWithDurationsTest(){
        val tag1 = createTag("tag1")
        val tag2 = createTag("tag2")
        assert(tag1 != null)
        assert(tag2 != null)

        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf(tag1!!))
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session4 = Session(40, Calendar.getInstance(), 0, mutableListOf(tag2!!))
        val session5 = Session(50, Calendar.getInstance(), 0, mutableListOf(tag2))
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)
        sessionDBService.addSession(session4)
        sessionDBService.addSession(session5)

        val tagsWithDurations = statsDBService.getAllTagsWithDurations()
        assertEquals(2, tagsWithDurations.size)
        assertEquals(tagsWithDurations[1].first.tagName, tag1.tagName)
        assertEquals(tagsWithDurations[1].first.id, tag1.id)
        assertEquals(tagsWithDurations[1].second, 60L)
        assertEquals(tagsWithDurations[0].first.tagName, tag2.tagName)
        assertEquals(tagsWithDurations[0].first.id, tag2.id)
        assertEquals(tagsWithDurations[0].second, 90L)
    }

    @Test
    fun getDurationForSessionsWithTagsTest(){
        val tag1 = createTag("tag1")
        val tag2 = createTag("tag2")
        val tag3 = createTag("tag3")
        assert(tag1 != null)
        assert(tag2 != null)
        assert(tag3 != null)

        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf(tag1!!))
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session4 = Session(40, Calendar.getInstance(), 0, mutableListOf(tag2!!))
        val session5 = Session(50, Calendar.getInstance(), 0, mutableListOf(tag2))
        val session6 = Session(60, Calendar.getInstance(), 0, mutableListOf(tag3!!))
        val session7 = Session(70, Calendar.getInstance(), 0, mutableListOf(tag3))
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)
        sessionDBService.addSession(session4)
        sessionDBService.addSession(session5)
        sessionDBService.addSession(session6)
        sessionDBService.addSession(session7)

        val duration = statsDBService.getDurationForSessionsWithTags(listOf(tag1.id, tag3.id))
        assertEquals(190L, duration)
    }

    @Test
    fun getDurationForSessionsWithTagsTestNoSessionsWithTagIDs(){
        val tag1 = createTag("tag1")
        val tag2 = createTag("tag2")
        val tag3 = createTag("tag3")
        assert(tag1 != null)
        assert(tag2 != null)
        assert(tag3 != null)

        val session1 = Session(10, Calendar.getInstance(), 0, mutableListOf(tag1!!))
        val session2 = Session(20, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session3 = Session(30, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session4 = Session(40, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session5 = Session(50, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session6 = Session(60, Calendar.getInstance(), 0, mutableListOf(tag1))
        val session7 = Session(70, Calendar.getInstance(), 0, mutableListOf(tag1))
        sessionDBService.addSession(session1)
        sessionDBService.addSession(session2)
        sessionDBService.addSession(session3)
        sessionDBService.addSession(session4)
        sessionDBService.addSession(session5)
        sessionDBService.addSession(session6)
        sessionDBService.addSession(session7)

        val duration = statsDBService.getDurationForSessionsWithTags(listOf(tag2!!.id, tag3!!.id))
        assertEquals(0L, duration)
    }

    private fun createTag(name: String): Tag? {
        val tag = Tag(name, 0)
        val id = tagDBService.addTag(tag)
        return tagDBService.getTagByID(id)
    }

}