package com.example.scoreboard.database

import android.content.Context
import android.provider.BaseColumns
import com.example.scoreboard.Tag

class StatsDBService(
    private val appContext: Context,
    private val databaseName: String = DatabaseConstants.DATABASE_NAME
) : ScoreboardDatabase(appContext, databaseName) {

    fun getTotalDuration(): Long {
        val db = this.readableDatabase
        val resultColumn = "total_duration"
        val projection =
            arrayOf("SUM(${DatabaseConstants.SessionsTable.DURATION_COLUMN}) as $resultColumn")
        val cursor = db.query(
            DatabaseConstants.SessionsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(resultColumn))
            cursor.close()
            db.close()
            return duration
        }
        return 0L
    }

    fun getDurationForTag(tagID: Long): Long {
        val db = this.readableDatabase
        val resultColumn = "total_duration"
        val projection =
            arrayOf("SUM(${DatabaseConstants.SessionsTable.DURATION_COLUMN}) as $resultColumn")
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
        if (cursor.moveToFirst()) {
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(resultColumn))
            cursor.close()
            db.close()
            return duration
        }
        return 0L
    }

    fun getAllTagsWithDurations(): List<Pair<Tag, Long>> {
        val tagList = TagDBService(appContext, databaseName).getAllTags()
        var tagDurationList = tagList.map { tag ->
            Pair(tag, getDurationForTag(tag.id))
        }
        tagDurationList = tagDurationList.sortedByDescending { it.second }
        return tagDurationList
    }

    fun getAllTagsWithDurations(page: Int, pageSize: Int): List<Pair<Tag, Long>> {
        val resultColumn = "total_duration"
        val db = this.readableDatabase

        val sessionIDalt = "id_from_sessions"
        val sessionDurationsSubQuery =
            "SELECT ${BaseColumns._ID} as $sessionIDalt, " +
                    "${DatabaseConstants.SessionsTable.DURATION_COLUMN} " +
                    "FROM ${DatabaseConstants.SessionsTable.TABLE_NAME} "

        val tagsDurationsSubquery = "SELECT ${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN}, " +
                "SUM(${DatabaseConstants.SessionsTable.DURATION_COLUMN}) as $resultColumn " +
                "FROM ($sessionDurationsSubQuery) " +
                "INNER JOIN ${DatabaseConstants.SessionTagTable.TABLE_NAME} " +
                "ON $sessionIDalt = ${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} " +
                "GROUP BY ${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} " +
                "ORDER BY $resultColumn DESC "

        val finalQuery = "(SELECT ${DatabaseConstants.TagsTable.NAME_COLUMN}, $resultColumn, ${BaseColumns._ID} " +
                "FROM ($tagsDurationsSubquery) " +
                "INNER JOIN ${DatabaseConstants.TagsTable.TABLE_NAME} " +
                "ON ${DatabaseConstants.TagsTable.TABLE_NAME}.${BaseColumns._ID} = ${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN}) "

        val projection = arrayOf(
            DatabaseConstants.TagsTable.NAME_COLUMN,
            resultColumn,
            BaseColumns._ID
        )
        val offset = (page - 1) * pageSize
        val limit = "$offset, $pageSize"

        val tagsWithDuration = mutableListOf<Pair<Tag, Long>>()

        val cursor = db.query(
            finalQuery,
            projection,
            null,
            null,
            null,
            null,
            null,
            limit
        )

        with(cursor){
            while(moveToNext()){
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val tagName = getString(getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
                val duration = getLong(getColumnIndexOrThrow(resultColumn))


                val tag = Tag(tagName, id)
                tagsWithDuration.add(Pair(tag, duration))
            }
        }
        return tagsWithDuration
    }

    fun getDurationForSessionsWithTags(tagIDs: List<Long>): Long {
        if (tagIDs.isEmpty()) return getTotalDuration()
        val db = this.readableDatabase
        val resultColumn = "total_duration"
        val projection =
            arrayOf("SUM(${DatabaseConstants.SessionsTable.DURATION_COLUMN}) as $resultColumn")

        val sessionIDsTableQuery =
            "SELECT ${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} " +
                    "FROM ${DatabaseConstants.SessionTagTable.TABLE_NAME} " +
                    "WHERE ${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} IN (${tagIDs.joinToString()}) " +
                    "GROUP BY ${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} " +
                    "HAVING COUNT(${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN}) = ${tagIDs.size}"

        val tableJoined = "($sessionIDsTableQuery) " +
                "INNER JOIN ${DatabaseConstants.SessionsTable.TABLE_NAME} " +
                "ON ${DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN} = ${BaseColumns._ID}"

        val cursor = db.query(
            tableJoined,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val duration = cursor.getLong(cursor.getColumnIndexOrThrow(resultColumn))
            cursor.close()
            db.close()
            return duration
        }
        db.close()
        return 0L
    }
}