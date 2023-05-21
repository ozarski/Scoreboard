package com.example.scoreboard.database

import android.content.Context
import com.example.scoreboard.session.Session
import com.example.scoreboard.session.SessionData

class SessionDBService(context: Context): ScoreboardDatabase(context) {

    fun addSession(session: Session): Long{
        TODO("Not yet implemented")
    }

    fun updateSession(session: Session){
        TODO("Not yet implemented")
    }

    fun deleteSessionByID(id: Long){
        TODO("Not yet implemented")
    }

    fun getSessionDataByID(id: Long): SessionData? {
        TODO("Not yet implemented")
    }

    fun getSessionWithTagsByID(id: Long): Session?{
        TODO("Not yet implemented")
    }

    fun getAllSessions(): List<Session>{
        TODO("Not yet implemented")
    }
}