package com.example.scoreboard.database

import android.content.Context
import android.provider.BaseColumns

class StatsDBService(context: Context): ScoreboardDatabase(context) {

    fun getTotalDuration(): Long{
        val db = this.readableDatabase
        val resultColumn = "total_duration"
        val projection = arrayOf("SUM(${DatabaseConstants.SessionsTable.DURATION_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        if(cursor.moveToFirst()){
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(resultColumn))
            cursor.close()
            db.close()
            return duration
        }
        return 0L
    }

    fun getDurationForTag(tagID: Long): Long{
        val db = this.readableDatabase
        val resultColumn = "total_duration"
        val projection = arrayOf("SUM(${DatabaseConstants.SessionsTable.DURATION_COLUMN}) as $resultColumn")
        val tableJoined = "${DatabaseConstants.SessionsTable.TABLE_NAME} " +
                "INNER JOIN ${DatabaseConstants.SessionTagTable.TABLE_NAME} " +
                "ON ${DatabaseConstants.SessionsTable.TABLE_NAME}.${BaseColumns._ID} = " +
                "${DatabaseConstants.SessionTagTable.TABLE_NAME}.${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN}"
        val selection = "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString())
        val cursor = db.query(
            tableJoined,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if(cursor.moveToFirst()){
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(resultColumn))
            cursor.close()
            db.close()
            return duration
        }
        return 0L
    }
}