package com.example.scoreboard

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scoreboard.database.DatabaseConstants
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.database.StatsDBService
import com.example.scoreboard.database.TagDBService
import com.example.scoreboard.session.Session
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
        statsDBService = StatsDBService(applicationContext)
        sessionDBService = SessionDBService(applicationContext)
        tagDBService = TagDBService(applicationContext)
    }

    @After
    fun tearDown() {
        statsDBService.close()
        sessionDBService.close()
        tagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
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
        assert(duration == 60L)
    }

    @Test
    fun getTotalDurationNoSessionsInDatabase(){
        val duration = statsDBService.getTotalDuration()
        assert(duration == 0L)
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
        assert(durationTag1 == 60L)
        assert(durationTag2 == 20L)
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
        assert(duration == 0L)
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
        assert(duration == 0L)
    }

    fun createTag(name: String): Tag?{
        val tag = Tag(name, 0)
        val id = tagDBService.addTag(tag)
        val addedTag = tagDBService.getTagByID(id)
        return addedTag
    }

}