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
            if(databaseName == DatabaseConstants.SCHEMA_CHECK_DATABASE_NAME) {
                return
            }
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

    fun checkDatabaseSchema(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(DatabaseConstants.ALL_TABLES_QUERY, null)
        val tables = mutableListOf<String>()
        while (cursor.moveToNext()) {
            println(cursor.getString(0))
            tables.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        if (!(tables.contains(DatabaseConstants.SessionsTable.TABLE_NAME) &&
                    tables.contains(DatabaseConstants.TagsTable.TABLE_NAME) &&
                    tables.contains(DatabaseConstants.SessionTagTable.TABLE_NAME))
        ) {
           return false
        }
        return checkSessionsTableSchema() &&
                checkTagsTableSchema() &&
                checkSessionTagTableSchema()
    }

    private fun checkSessionsTableSchema(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            DatabaseConstants.tableInfoQuery(DatabaseConstants.SessionsTable.TABLE_NAME),
            null
        )
        val columns = mutableListOf<Pair<String, String>>()
        while (cursor.moveToNext()) {
            columns.add(Pair(cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()
        db.close()
        columns.indexOfFirst { it.first == DatabaseConstants.SessionsTable.DURATION_COLUMN }.run{
            if (this == -1) {
                return false
            }
            columns[this].second == "INTEGER"
        }
        columns.indexOfFirst { it.first == DatabaseConstants.SessionsTable.DATE_COLUMN }.run{
            if (this == -1) {
                return false
            }
            columns[this].second == "INTEGER"
        }
        return true
    }

    private fun checkTagsTableSchema(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            DatabaseConstants.tableInfoQuery(DatabaseConstants.TagsTable.TABLE_NAME),
            null
        )
        val columns = mutableListOf<Pair<String, String>>()
        while (cursor.moveToNext()) {
            columns.add(Pair(cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()
        db.close()
        columns.indexOfFirst { it.first == DatabaseConstants.TagsTable.NAME_COLUMN }.run{
            if (this == -1) {
                return false
            }
            columns[this].second == "TEXT"
        }
        return true
    }

    private fun checkSessionTagTableSchema(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            DatabaseConstants.tableInfoQuery(DatabaseConstants.SessionTagTable.TABLE_NAME),
            null
        )
        val columns = mutableListOf<Pair<String, String>>()
        while (cursor.moveToNext()) {
            columns.add(Pair(cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()
        db.close()
        columns.indexOfFirst { it.first == DatabaseConstants.SessionTagTable.SESSION_ID_COLUMN }.run{
            if (this == -1) {
                return false
            }
            columns[this].second == "INTEGER"
        }
        columns.indexOfFirst { it.first == DatabaseConstants.SessionTagTable.TAG_ID_COLUMN }.run{
            if (this == -1) {
                return false
            }
            columns[this].second == "INTEGER"
        }
        return true
    }
}