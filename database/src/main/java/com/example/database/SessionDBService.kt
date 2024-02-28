package com.example.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.example.base.Tag
import com.example.base.session.Session
import com.example.base.session.SessionData
import com.example.database.DatabaseConstants.DEFAULT_PAGE_SIZE
import com.ozarskiapps.global.setCalendarToDayEnd
import java.util.Calendar

class SessionDBService(
    private val appContext: Context,
    private val databaseName: String = DatabaseConstants.DATABASE_NAME
) : ScoreboardDatabase(appContext, databaseName) {

    fun addSession(session: Session): Long {
        if (session.getDuration() < 0) {
            throw Exception("Session duration cannot be negative")
        }

        validateSessionDate(session.getDate())
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.SessionsTable.DURATION_COLUMN, session.getDuration())
            put(DatabaseConstants.SessionsTable.DATE_COLUMN, session.getDate().timeInMillis)
        }

        session.id = this.writableDatabase.insert(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            null,
            contentValues
        )

        SessionTagDBService(appContext, databaseName).run {
            session.tags.forEach {
                addTagToSession(it.id, session.id)
            }
        }
        return session.id
    }

    fun updateSession(session: Session) {
        if (session.getDuration() < 0) {
            throw Exception("Session duration cannot be negative")
        }

        validateSessionDate(session.getDate())

        val contentValues = ContentValues().apply {
            put(DatabaseConstants.SessionsTable.DURATION_COLUMN, session.getDuration())
            put(DatabaseConstants.SessionsTable.DATE_COLUMN, session.getDate().timeInMillis)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(session.id.toString())

        this.writableDatabase.update(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            contentValues,
            selection,
            selectionArgs
        )
        SessionTagDBService(appContext, databaseName).apply {
            deleteSessionTagsOnSessionDelete(session.id)
            session.tags.forEach {
                addTagToSession(it.id, session.id)
            }
        }
    }

    fun deleteSessionByID(id: Long) {
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        this.writableDatabase.delete(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            selection,
            selectionArgs
        )
        SessionTagDBService(appContext, databaseName).deleteSessionTagsOnSessionDelete(id)
    }

    fun getSessionDataByID(id: Long): SessionData? {
        val projection = arrayOf(
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        with(cursor) {
            if (!moveToFirst()) return null

            return SessionData(
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN)),
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN)),
                id
            ).also {
                close()
            }
        }
    }

    fun getSessionWithTagsByID(id: Long): Session? {
        val projection = arrayOf(
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        with(cursor) {
            if (!moveToFirst()) return null

            return getSession(cursor, id).also {
                close()
            }
        }
    }

    fun getAllSessions(): List<Session> {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val sessions = mutableListOf<Session>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                getSession(cursor, id).also { session ->
                    sessions.add(session)
                }
            }
            close()
        }
        return sessions
    }

    fun getSessionsByIDs(sessionIDs: List<Long>): List<Session> {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} IN (${sessionIDs.joinToString(",")})"
        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            null,
            null,
            null,
            null
        )
        val sessions = mutableListOf<Session>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                getSession(cursor, id).also { session ->
                    sessions.add(session)
                }
            }
            close()
        }
        return sessions
    }

    fun getSessionsByIDs(
        sessionIDs: List<Long>,
        page: Int,
        pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<Session> {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} IN (${sessionIDs.joinToString(",")})"
        val orderBy = "${DatabaseConstants.SessionsTable.DATE_COLUMN} DESC"
        val limit = "${(page - 1) * pageSize}, $pageSize"
        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            null,
            null,
            null,
            orderBy,
            limit
        )
        val sessions = mutableListOf<Session>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                getSession(cursor, id).also { session ->
                    sessions.add(session)
                }
            }
            close()
        }
        return sessions
    }

    fun getAllSessions(page: Int, pageSize: Int = DEFAULT_PAGE_SIZE): List<Session> {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )

        val orderBy = "${DatabaseConstants.SessionsTable.DATE_COLUMN} DESC"
        val limit = "${(page - 1) * pageSize}, $pageSize"

        val cursor = this.readableDatabase.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            orderBy,
            limit
        )
        val sessions = mutableListOf<Session>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                getSession(cursor, id).also { session ->
                    sessions.add(session)
                }
            }
            close()
        }
        return sessions
    }

    private fun getSession(cursor: Cursor, id: Long): Session {
        with(cursor) {
            val duration =
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            val date =
                getLong(getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            val sessionTags = mutableListOf<Tag>().apply {
                val sessionTagIDs =
                    SessionTagDBService(appContext, databaseName).getTagIDsForSession(id)
                sessionTagIDs.forEach {
                    TagDBService(appContext, databaseName).getTagByID(it)?.let { tag ->
                        add(tag)
                    }
                }
            }
            return Session(
                duration,
                Calendar.getInstance().apply { timeInMillis = date },
                id,
                sessionTags
            )
        }
    }

    private fun validateSessionDate(date: Calendar) {
        val today = Calendar.getInstance()
        setCalendarToDayEnd(today)
        if (date.after(today)) {
            throw Exception("Session date cannot be in the future")
        }
    }
}