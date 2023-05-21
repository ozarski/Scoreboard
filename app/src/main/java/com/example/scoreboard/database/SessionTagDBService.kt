package com.example.scoreboard.database

import android.content.Context

class SessionTagDBService(context: Context) : ScoreboardDatabase(context) {

    fun addTagToSession(tagID: Long, sessionID: Long) {
        TODO("Not yet implemented")
    }

    fun removeTagFromSession(tagID: Long, sessionID: Long) {
        TODO("Not yet implemented")
    }

    fun getSessionIDsForTag(tagID: Long): List<Long> {
        TODO("Not yet implemented")
    }

    fun getTagIDsForSession(sessionID: Long): List<Long> {
        TODO("Not yet implemented")
    }

    fun deleteSessionTagsOnSessionDelete(sessionID: Long) {
        TODO("Not yet implemented")
    }

    fun deleteSessionTagsOnTagDelete(tagID: Long) {
        TODO("Not yet implemented")
    }

}