package com.example.scoreboard.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.scoreboard.Tag
import com.example.scoreboard.session.Session
import java.util.Calendar

open class ScoreboardDatabase(
    val context: Context,
    databaseName: String = DatabaseConstants.DATABASE_NAME
) : SQLiteOpenHelper(
    context,
    databaseName,
    null,
    DatabaseConstants.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            createSessionsTable(db)
            createTagsTable(db)
            createSessionsTagsTable(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    private fun createSessionsTable(db: SQLiteDatabase) {
        db.execSQL(DatabaseConstants.CREATE_SESSIONS_TABLE)
    }

    private fun createTagsTable(db: SQLiteDatabase) {
        db.execSQL(DatabaseConstants.CREATE_TAGS_TABLE)
    }

    private fun createSessionsTagsTable(db: SQLiteDatabase) {
        db.execSQL(DatabaseConstants.CREATE_SESSION_TAG_TABLE)
    }

    fun generateMockData() {
        val tagDBService = TagDBService(context)
        val sessionDBService = SessionDBService(context)
        val tag1 = Tag("tag1", -1)
        tag1.id = tagDBService.addTag(tag1)
        val tag2 = Tag("tag2", -1)
        tag2.id = tagDBService.addTag(tag2)
        val tag3 = Tag("tag3", -1)
        tag3.id = tagDBService.addTag(tag3)

        val session1 = Session(3600, Calendar.getInstance(), -1, mutableListOf(tag1))
        sessionDBService.addSession(session1)
        val session2 = Session(30000, Calendar.getInstance(), -1, mutableListOf(tag2))
        sessionDBService.addSession(session2)
        val session3 = Session(20000, Calendar.getInstance(), -1, mutableListOf(tag3))
        sessionDBService.addSession(session3)
    }
}