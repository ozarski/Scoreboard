package com.example.tests

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.database.DatabaseConstants
import com.example.database.ScoreboardDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

class ScoreboardDatabaseTests {
    private lateinit var appContext: Context
    private lateinit var dbService: ScoreboardDatabase

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        dbService = ScoreboardDatabase(appContext, DatabaseConstants.TEST_DATABASE_NAME)
    }

    @After
    fun tearDown() {
        dbService.close()
        appContext.deleteDatabase(DatabaseConstants.TEST_DATABASE_NAME)
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
                    "AND name='${DatabaseConstants.SessionTagTable.TABLE_NAME}'",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(DatabaseConstants.SessionTagTable.TABLE_NAME, cursor.getString(0))
        }
    }

    @Test
    fun checkSchemaTest() {
        assertTrue(dbService.checkDatabaseSchema())
    }

    @Test
    fun checkSchemaTestFail() {
        val db = ScoreboardDatabase(
            context = appContext,
            databaseName = DatabaseConstants.SCHEMA_TEST_DATABASE_NAME
        )
        copyDatabaseFromAssets(appContext)
        assertFalse(db.checkDatabaseSchema())
    }

    private fun copyDatabaseFromAssets(context: Context) {
        val dbPath = context.getDatabasePath(DatabaseConstants.SCHEMA_TEST_DATABASE_NAME)
        if (!dbPath.exists()) {
            val inputStream = context.assets.open(DatabaseConstants.SCHEMA_TEST_DATABASE_NAME)
            val outputStream = FileOutputStream(dbPath)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        }
    }
}