package com.example.scoreboard.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.scoreboard.Tag

class TagDBService(context: Context) : ScoreboardDatabase(context) {

    fun addTag(tag: Tag): Long {
        if (tag.tagName.isEmpty()) {
            return -1L
        }
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagsTable.NAME_COLUMN, tag.tagName)
        }
        val tagID = db.insert(DatabaseConstants.TagsTable.TABLE_NAME, null, contentValues)
        db.close()
        return tagID
    }

    fun updateTag(tag: Tag) {
        if (tag.tagName.isEmpty()) {
            return
        }
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagsTable.NAME_COLUMN, tag.tagName)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(tag.id.toString())
        db.update(
            DatabaseConstants.TagsTable.TABLE_NAME,
            contentValues,
            selection,
            selectionArgs
        )
        db.close()
    }

    fun deleteTagByID(id: Long) {
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        db.delete(DatabaseConstants.TagsTable.TABLE_NAME, selection, selectionArgs)
        db.close()
        SessionTagDBService(context).deleteSessionTagsOnTagDelete(id)
    }

    fun getTagByID(id: Long): Tag? {
        val db = this.readableDatabase
        val projection = arrayOf(
            DatabaseConstants.TagsTable.NAME_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.query(
            DatabaseConstants.TagsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            val tagName =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
            return Tag(tagName, id)
        }
        return null
    }

    fun getAllTags(): List<Tag>{
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagsTable.NAME_COLUMN
        )
        val cursor = db.query(
            DatabaseConstants.TagsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val tags = mutableListOf<Tag>()
        while(cursor.moveToNext()){
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val tagName =
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
            tags.add(Tag(tagName, id))
            println("Tag: $id, $tagName")
        }
        cursor.close()
        db.close()
        return tags
    }

    private fun deleteSessionsOnTagDelete(tagID: Long){
        val db = this.writableDatabase
        val projection = arrayOf(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN)
        val selection = "${DatabaseConstants.SessionTagTable.TAG_ID_COLUMN} = ?"
        val selectionArgs = arrayOf(tagID.toString())
        val cursor = db.query(
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
                val sessionID = getLong(getColumnIndexOrThrow(DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN))
                sessionIDs.add(sessionID)
            }
        }
        cursor.close()
        db.close()
        for(sessionID in sessionIDs){
            SessionDBService(context).deleteSessionByID(sessionID)
        }
    }
}