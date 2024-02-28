package com.example.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.base.Tag
import com.example.database.DatabaseConstants.DEFAULT_PAGE_SIZE

class TagDBService(context: Context, databaseName: String = DatabaseConstants.DATABASE_NAME) :
    ScoreboardDatabase(context, databaseName) {

    fun addTag(tag: Tag): Long {
        if (tag.tagName.isEmpty()) {
            println("Tag name cannot be empty")
            return -1L
        }
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagsTable.NAME_COLUMN, tag.tagName)
        }
        return this.writableDatabase.insert(
            DatabaseConstants.TagsTable.TABLE_NAME,
            null,
            contentValues
        )
    }

    fun updateTag(tag: Tag) {
        if (tag.tagName.isEmpty()) {
            return
        }
        val contentValues = ContentValues().apply {
            put(DatabaseConstants.TagsTable.NAME_COLUMN, tag.tagName)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(tag.id.toString())

        this.writableDatabase.update(
            DatabaseConstants.TagsTable.TABLE_NAME,
            contentValues,
            selection,
            selectionArgs
        )
    }

    fun deleteTagByID(id: Long) {
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        this.writableDatabase.delete(
            DatabaseConstants.TagsTable.TABLE_NAME,
            selection,
            selectionArgs
        )
        SessionTagDBService(context, databaseName).deleteSessionTagsOnTagDelete(id)
    }

    fun getTagByID(id: Long): Tag? {
        val projection = arrayOf(
            DatabaseConstants.TagsTable.NAME_COLUMN
        )
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = this.readableDatabase.query(
            DatabaseConstants.TagsTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        with(cursor) {
            if (moveToFirst()) {
                val tagName =
                    getString(getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
                return Tag(tagName, id).also { close() }
            }
        }
        return null
    }

    fun getAllTags(): List<Tag> {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagsTable.NAME_COLUMN
        )
        val cursor = this.readableDatabase.query(
            DatabaseConstants.TagsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val tags = mutableListOf<Tag>()
        with(cursor){
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val tagName =
                    cursor.getString(getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
                tags.add(Tag(tagName, id))
            }
            close()
        }
        return tags
    }

    fun getAllTags(page: Int, pageSize: Int = DEFAULT_PAGE_SIZE): List<Tag> {
        val projection = arrayOf(
            BaseColumns._ID,
            DatabaseConstants.TagsTable.NAME_COLUMN
        )
        val orderBy = BaseColumns._ID
        val limit = "${(page - 1) * pageSize}, $pageSize"
        val tags = mutableListOf<Tag>()

        val cursor = this.readableDatabase.query(
            DatabaseConstants.TagsTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            orderBy,
            limit
        )
        with(cursor){
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val tagName =
                    getString(getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
                tags.add(Tag(tagName, id))
            }
            close()
        }
        return tags
    }
}