package com.example.scoreboard

import android.content.Context
import android.provider.BaseColumns
import androidx.test.platform.app.InstrumentationRegistry
import com.example.base.Tag
import com.ozarskiapps.scoreboard.database.DatabaseConstants
import com.ozarskiapps.scoreboard.database.TagDBService
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
        tagDBService = TagDBService(applicationContext, DatabaseConstants.TEST_DATABASE_NAME)
    }

    @After
    fun tearDown() {
        tagDBService.close()
        applicationContext.deleteDatabase(DatabaseConstants.TEST_DATABASE_NAME)
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

    @Test
    fun getAllTagsTest(){
        val tag1 = Tag("tag_name1", -1)
        val id1 = tagDBService.addTag(tag1)
        val addedTag1 = Tag("tag_name1", id1)
        assertTagAdded(addedTag1)

        val tag2 = Tag("tag_name2", -1)
        val id2 = tagDBService.addTag(tag2)
        val addedTag2 = Tag("tag_name2", id2)
        assertTagAdded(addedTag2)

        val tag3 = Tag("tag_name3", -1)
        val id3 = tagDBService.addTag(tag3)
        val addedTag3 = Tag("tag_name3", id3)
        assertTagAdded(addedTag3)

        val tags = tagDBService.getAllTags()
        assertEquals(3, tags.size)
        assertEquals(addedTag1.tagName, tags[0].tagName)
        assertEquals(addedTag2.tagName, tags[1].tagName)
        assertEquals(addedTag3.tagName, tags[2].tagName)
        assertEquals(addedTag1.id, tags[0].id)
        assertEquals(addedTag2.id, tags[1].id)
        assertEquals(addedTag3.id, tags[2].id)
    }

    @Test
    fun getAllTagsTestNoTagsInDatabase(){
        val tags = tagDBService.getAllTags()
        assertEquals(0, tags.size)
    }

    @Test
    fun getAllTagsPagingTest(){
        val tag1 = Tag("tag_name1", -1)
        val id1 = tagDBService.addTag(tag1)
        val addedTag1 = Tag("tag_name1", id1)
        assertTagAdded(addedTag1)

        val tag2 = Tag("tag_name2", -1)
        val id2 = tagDBService.addTag(tag2)
        val addedTag2 = Tag("tag_name2", id2)
        assertTagAdded(addedTag2)

        val tag3 = Tag("tag_name3", -1)
        val id3 = tagDBService.addTag(tag3)
        val addedTag3 = Tag("tag_name3", id3)
        assertTagAdded(addedTag3)

        val tags = tagDBService.getAllTags(1, 2)
        assertEquals(2, tags.size)
        assertEquals(addedTag1.tagName, tags[0].tagName)
        assertEquals(addedTag2.tagName, tags[1].tagName)
        assertEquals(addedTag1.id, tags[0].id)
        assertEquals(addedTag2.id, tags[1].id)
    }

    @Test
    fun getAllTagsPagingTestInvalidPaging(){
        val tag1 = Tag("tag_name1", -1)
        val id1 = tagDBService.addTag(tag1)
        val addedTag1 = Tag("tag_name1", id1)
        assertTagAdded(addedTag1)

        val tags = tagDBService.getAllTags(2, 2)
        assertEquals(0, tags.size)
    }
}