package com.example.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
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

        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.SessionsTable.DURATION_COLUMN, session.getDuration())
            put(DatabaseConstants.SessionsTable.DATE_COLUMN, session.getDate().timeInMillis)
        }

        val sessionID = db.insert(DatabaseConstants.SessionsTable.TABLE_NAME, null, contentValues)
        session.tags.forEach {
            SessionTagDBService(appContext, databaseName).addTagToSession(it.id, sessionID)
        }
        db.close()
        return sessionID
    }

    fun updateSession(session: Session) {
        if (session.getDuration() < 0) {
            throw Exception("Session duration cannot be negative")
        }

        validateSessionDate(session.getDate())

        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.SessionsTable.DURATION_COLUMN, session.getDuration())
            put(DatabaseConstants.SessionsTable.DATE_COLUMN, session.getDate().timeInMillis)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(session.id.toString())
        db.update(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            contentValues,
            selection,
            selectionArgs
        )
        SessionTagDBService(appContext, databaseName).deleteSessionTagsOnSessionDelete(session.id)
        session.tags.forEach {
            SessionTagDBService(appContext, databaseName).addTagToSession(it.id, session.id)
        }
        db.close()
    }

    fun deleteSessionByID(id: Long) {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            selection,
            selectionArgs
        )
        SessionTagDBService(appContext, databaseName).deleteSessionTagsOnSessionDelete(id)
        db.close()
    }

    fun getSessionDataByID(id: Long): SessionData? {
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val duration =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            val date =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            val sessionData = SessionData(duration, date, id)
            cursor.close()
            return sessionData
        }
        cursor.close()
        return null
    }

    fun getSessionWithTagsByID(id: Long): Session? {
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val duration =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            val date =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            val session = Session(
                duration,
                Calendar.getInstance().apply { timeInMillis = date },
                id,
                mutableListOf()
            )
            val sessionTagIDs =
                SessionTagDBService(appContext, databaseName).getTagIDsForSession(id)
            sessionTagIDs.forEach {
                val tag = TagDBService(appContext, databaseName).getTagByID(it)
                if (tag != null) {
                    session.tags.add(tag)
                }
            }
            cursor.close()
            return session
        }
        cursor.close()
        return null
    }

    fun getAllSessions(): List<Session> {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val cursor = db.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val sessions = mutableListOf<Session>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val duration =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            val date =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            val session = Session(
                duration,
                Calendar.getInstance().apply { timeInMillis = date },
                id,
                mutableListOf()
            )
            val sessionTagIDs =
                SessionTagDBService(appContext, databaseName).getTagIDsForSession(id)
            sessionTagIDs.forEach {
                val tag = TagDBService(appContext, databaseName).getTagByID(it)
                if (tag != null) {
                    session.tags.add(tag)
                }
            }
            sessions.add(session)
        }
        cursor.close()
        return sessions
    }

    fun getSessionsByIDs(sessionIDs: List<Long>): List<Session> {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )
        val selection = "${BaseColumns._ID} IN (${sessionIDs.joinToString(",")})"
        val cursor = db.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            selection,
            null,
            null,
            null,
            null
        )
        val sessions = mutableListOf<Session>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val duration =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            val date =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            val session = Session(
                duration,
                Calendar.getInstance().apply { timeInMillis = date },
                id,
                mutableListOf()
            )
            val sessionTagIDs =
                SessionTagDBService(appContext, databaseName).getTagIDsForSession(id)
            sessionTagIDs.forEach {
                val tag = TagDBService(appContext, databaseName).getTagByID(it)
                if (tag != null) {
                    session.tags.add(tag)
                }
            }
            sessions.add(session)
        }
        cursor.close()
        return sessions
    }

    fun getSessionsByIDs(sessionIDs: List<Long>, page: Int, pageSize: Int = DEFAULT_PAGE_SIZE): List<Session>{
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
        with(cursor){
            while(moveToNext()){
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val duration =
                    getLong(getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
                val date =
                    getLong(getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
                val session = Session(
                    duration,
                    Calendar.getInstance().apply { timeInMillis = date },
                    id,
                    mutableListOf()
                )
                val sessionTagIDs =
                    SessionTagDBService(appContext, databaseName).getTagIDsForSession(id)
                sessionTagIDs.forEach {
                    val tag = TagDBService(appContext, databaseName).getTagByID(it)
                    if (tag != null) {
                        session.tags.add(tag)
                    }
                }
                sessions.add(session)

            }
        }
        return sessions
    }

    fun getAllSessions(page: Int, pageSize: Int = DEFAULT_PAGE_SIZE): List<Session> {
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.SessionsTable.DURATION_COLUMN,
            DatabaseConstants.SessionsTable.DATE_COLUMN
        )

        val orderBy = "${DatabaseConstants.SessionsTable.DATE_COLUMN} DESC"
        val limit = "${(page - 1) * pageSize}, $pageSize"

        val cursor = db.query(
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
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val duration =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DURATION_COLUMN))
            val date =
                cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.SessionsTable.DATE_COLUMN))
            val session = Session(
                duration,
                Calendar.getInstance().apply { timeInMillis = date },
                id,
                mutableListOf()
            )
            val sessionTagIDs =
                SessionTagDBService(appContext, databaseName).getTagIDsForSession(id)
            sessionTagIDs.forEach {
                val tag = TagDBService(appContext, databaseName).getTagByID(it)
                if (tag != null) {
                    session.tags.add(tag)
                }
            }
            sessions.add(session)
        }
        cursor.close()
        return sessions
    }

    fun validateSessionDate(date: Calendar){
        val today = Calendar.getInstance()
        setCalendarToDayEnd(today)
        if (date.after(today)) {
            throw Exception("Session date cannot be in the future")
        }
    }
}