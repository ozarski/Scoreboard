package com.example.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        db?.let {
            createSessionsTable(it)
            createTagsTable(it)
            createSessionsTagsTable(it)
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
}