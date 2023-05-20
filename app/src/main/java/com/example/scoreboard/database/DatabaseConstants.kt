package com.example.scoreboard.database

import android.provider.BaseColumns


object DatabaseConstants {

    const val DATABASE_NAME = "scoreboard.db"
    const val DATABASE_VERSION = 1

    const val CREATE_SESSIONS_TABLE = "CREATE TABLE ${SessionsTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${SessionsTable.DURATION_COLUMN} INTEGER NOT NULL, " +
            "${SessionsTable.DATE_COLUMN} INTEGER NOT NULL)"

    const val CREATE_TAGS_TABLE = "CREATE TABLE ${TagsTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
            "${TagsTable.NAME_COLUMN} TEXT NOT NULL)"

    const val CREATE_SESSIONS_TAGS_TABLE = "CREATE TABLE ${SessionsTagsTable.TABLE_NAME} (" +
            "${SessionsTagsTable.SESSION_ID_COLUMN} INTEGER NOT NULL," +
            "${SessionsTagsTable.TAG_ID_COLUMN} INTEGER NOT NULL)"

    object SessionsTable {
        const val TABLE_NAME = "WorkSessions"
        const val DURATION_COLUMN = "duration"
        const val DATE_COLUMN = "date"
    }

    object TagsTable {
        const val TABLE_NAME = "Tags"
        const val NAME_COLUMN = "name"
    }

    object SessionsTagsTable {
        const val TABLE_NAME = "SessionsTags"
        const val SESSION_ID_COLUMN = "session_id"
        const val TAG_ID_COLUMN = "tag_id"
    }
}