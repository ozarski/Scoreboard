package com.example.scoreboard

import android.content.Context
import android.provider.BaseColumns
import androidx.test.platform.app.InstrumentationRegistry
import com.example.scoreboard.database.DatabaseConstants
import com.example.scoreboard.database.TagDBService
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test

class TagDBServiceTests {

    private lateinit var tagDBService: TagDBService
    private lateinit var applicationContext: Context

    @Before
    fun setUp() {
        applicationContext = InstrumentationRegistry.getInstrumentation().targetContext
        tagDBService = TagDBService(applicationContext)
    }

    @After
    fun tearDown() {
        tagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.DATABASE_NAME)
    }

    @Test
    fun addTagTest(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)
        val cursor = tagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.TagsTable.TABLE_NAME} " +
                    "WHERE ${BaseColumns._ID} = $id",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(
                "tag_name",
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
            )
        }
    }

    @Test
    fun addTagFailEmptyName(){
        val tag = Tag("", -1)
        val id = tagDBService.addTag(tag)
        assertEquals(-1, id)
    }

    @Test
    fun getTagByIDTest(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)

        val addedTag = Tag("tag_name", id)
        assertTagAdded(addedTag)

        val tagFromDB = tagDBService.getTagByID(id)
        assertEquals(tag.tagName, tagFromDB?.tagName)
    }

    @Test
    fun getTagByIDFail(){
        val tagFromDB = tagDBService.getTagByID(-1)
        assertEquals(null, tagFromDB)
    }

    @Test
    fun updateTagTest(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)

        val addedTag = Tag("tag_name", id)
        assertTagAdded(addedTag)

        val updatedTag = Tag("new_tag_name", id)
        tagDBService.updateTag(updatedTag)

        val updated = tagDBService.getTagByID(id)
        assertEquals(updatedTag.tagName, updated?.tagName)
    }

    @Test
    fun updateTagTestFailInvalidID(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)

        val addedTag = Tag("tag_name", id)
        assertTagAdded(addedTag)

        val updatedTag = Tag("new_tag_name", -1)
        tagDBService.updateTag(updatedTag)

        val updated = tagDBService.getTagByID(id)
        assertEquals(tag.tagName, updated?.tagName)
    }

    @Test
    fun updateTagTestFailEmptyName(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)

        val addedTag = Tag("tag_name", id)
        assertTagAdded(addedTag)

        val updatedTag = Tag("", id)
        tagDBService.updateTag(updatedTag)

        val updated = tagDBService.getTagByID(id)
        assertEquals(tag.tagName, updated?.tagName)
    }

    @Test
    fun deleteTagByIDTest(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)

        val addedTag = Tag("tag_name", id)
        assertTagAdded(addedTag)

        tagDBService.deleteTagByID(id)

        val added = tagDBService.getTagByID(id)
        assertEquals(null, added)
    }

    @Test
    fun deleteTagByIDFailInvalidID(){
        val tag = Tag("tag_name", -1)
        val id = tagDBService.addTag(tag)

        val addedTag = Tag("tag_name", id)
        assertTagAdded(addedTag)

        tagDBService.deleteTagByID(-1)

        val added = tagDBService.getTagByID(id)
        assertEquals(tag.tagName, added?.tagName)
    }

    private fun assertTagAdded(tag: Tag){
        val cursor = tagDBService.readableDatabase.rawQuery(
            "SELECT * FROM ${DatabaseConstants.TagsTable.TABLE_NAME} " +
                    "WHERE ${BaseColumns._ID} = ${tag.id}",
            null
        )
        cursor.use {
            assertTrue(cursor.moveToFirst())
            assertEquals(
                tag.tagName,
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.TagsTable.NAME_COLUMN))
            )
        }
    }
}