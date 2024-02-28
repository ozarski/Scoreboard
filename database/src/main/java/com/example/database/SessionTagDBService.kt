package com.example.database

import android.content.ContentValues
import android.content.Context
import com.example.base.session.Session
import com.example.database.DatabaseConstants.DEFAULT_PAGE_SIZE
import java.security.InvalidParameterException

class SessionTagDBService(
    context: Context,
    databaseName: String = DatabaseConstants.DATABASE_NAME
) : ScoreboardDatabase(context, databaseName) {

    fun addTagToSession(tagID: Long, sessionID: Long) {
        if (tagID < 0 || sessionID < 0) {
            println("Invalid tagID or sessionID")
            return
        }
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.SessionTagTable.TAG_ID_COLUMN, tagID)
            put(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN, sessionID)
        }
        this.writableDatabase.insert(
            DatabaseConstants.SessionTagTable.TABLE_NAME,
            null,
            contentValues
        )
    }

    fun removeTagFromSession(tagID: Long, sessionID: Long) {
        val selection =
            "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} = ? " +
                    "AND ${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString(), sessionID.toString())
        this.writableDatabase.delete(
            DatabaseConstants.SessionTagTable.TABLE_NAME,
            selection,
            selectionArgs
        )
    }

    fun getSessionIDsForTag(tagID: Long): List<Long> {
        val projection = arrayOf(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)
        val selection = "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString())

        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionTagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val sessionIDs = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)).also {
                    sessionIDs.add(it)
                }
            }
            close()
        }
        return sessionIDs
    }

    fun getTagIDsForSession(sessionID: Long): List<Long> {
        val projection = arrayOf(DatabaseConstants.SessionTagTable.TAG_ID_COLUMN)
        val selection = "${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(sessionID.toString())

        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionTagTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val tagIDs = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.TAG_ID_COLUMN)).also {
                    tagIDs.add(it)
                }
            }
            close()
        }
        return tagIDs
    }

    fun getSessionsForTagIDs(tagIDs: List<Long>): List<Session> {
        if (tagIDs.isEmpty()) {
            return SessionDBService(context, databaseName).getAllSessions()
        }
        val projection = arrayOf(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)
        val selection =
            "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} IN (${tagIDs.joinToString(", ")})"
        val having =
            "COUNT(${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN}) = ${tagIDs.size}"

        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionTagTable.TABLE_NAME,
            projection,
            selection,
            null,
            DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN,
            having,
            null
        )

        val sessionIDs = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)).also {
                    sessionIDs.add(it)
                }
            }
            close()
        }
        return SessionDBService(context, databaseName).getSessionsByIDs(sessionIDs)
    }

    fun getSessionsForTagIDs(
        tagIDs: List<Long>,
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<Session> {
        if (tagIDs.isEmpty()) {
            return SessionDBService(context, databaseName).getAllSessions(page, pageSize)
        }
        val projection = arrayOf(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)
        val selection =
            "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} IN (${tagIDs.joinToString(", ")})"
        val having =
            "COUNT(${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN}) = ${tagIDs.size}"

        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionTagTable.TABLE_NAME,
            projection,
            selection,
            null,
            DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN,
            having,
            null
        )

        val sessionIDs = mutableListOf<Long>()
        with(cursor) {
            while (moveToNext()) {
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)).also {
                    sessionIDs.add(it)
                }
            }
            close()
        }
        return SessionDBService(context, databaseName).getSessionsByIDs(sessionIDs, page, pageSize)
    }


    fun deleteSessionTagsOnSessionDelete(sessionID: Long) {
        val selection = "${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(sessionID.toString())
        this.writableDatabase.delete(DatabaseConstants.SessionTagTable.TABLE_NAME, selection, selectionArgs)
    }

    fun deleteSessionTagsOnTagDelete(tagID: Long) {
        val selection = "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString())
        this.writableDatabase.delete(DatabaseConstants.SessionTagTable.TABLE_NAME, selection, selectionArgs)
    }

}