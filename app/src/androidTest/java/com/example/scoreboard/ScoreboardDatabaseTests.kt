package com.example.scoreboard

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scoreboard.database.DatabaseConstants
import com.example.scoreboard.database.ScoreboardDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class ScoreboardDatabaseTests {
    private lateinit var appContext: Context
    private lateinit var dbService: ScoreboardDatabase

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        dbService = ScoreboardDatabase(appContext)
    }

    @After
    fun tearDown() {
        dbService.close()
        appContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun createDatabaseTest() {
        assertNotNull(dbService.writableDatabase)
    }

    @Test
    fun createSessionsTableTest() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' " +
                    "AND name='${DatabaseConstants.SessionsTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(DatabaseConstants.SessionsTable.TABLE_NAME, cursor.getString(0))
        }
    }

    @Test
    fun createTagsTableTest() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' " +
                    "AND name='${DatabaseConstants.TagsTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(DatabaseConstants.TagsTable.TABLE_NAME, cursor.getString(0))
        }
    }

    @Test
    fun createSessionsTagsTableTest() {
        val cursor = dbService.readableDatabase.rawQuery(
            "SELECT name " +
                    "FROM sqlite_master WHERE type='table' " +
                    "AND name='${DatabaseConstants.SessionsTagsTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(DatabaseConstants.SessionsTagsTable.TABLE_NAME, cursor.getString(0))
        }
    }
}