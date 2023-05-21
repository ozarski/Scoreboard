package com.example.scoreboard.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

open class ScoreboardDatabase(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseConstants.DATABASE_NAME,
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

    fun createSessionsTable(db: SQLiteDatabase) {
        db.execSQL(DatabaseConstants.CREATE_SESSIONS_TABLE)
    }

    fun createTagsTable(db: SQLiteDatabase) {
        db.execSQL(DatabaseConstants.CREATE_TAGS_TABLE)
    }

    fun createSessionsTagsTable(db: SQLiteDatabase) {
        db.execSQL(DatabaseConstants.CREATE_SESSION_TAG_TABLE)
    }
}