package com.example.scoreboard

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.popups.SessionDetailsPopup
import com.example.scoreboard.session.Session

class HistoryTab(val context: Context) : ComponentActivity() {

    private lateinit var sessions: SnapshotStateList<Session>
    private lateinit var sessionDetailsPopupVisible: MutableState<Boolean>
    private var popupSessionID = 0L

    @Composable
    fun GenerateLayout() {
        sessions = remember { SnapshotStateList() }
        sessionDetailsPopupVisible = remember { mutableStateOf(false) }
        var tempSessions = SessionDBService(context).getAllSessions()
        tempSessions = tempSessions.sortedByDescending { it.getDate().timeInMillis }
        sessions.clear()
        sessions.addAll(tempSessions)
        HistoryTabLayout()
    }

    @Composable
    fun HistoryTabLayout() {
        if (MainActivity.historyDataUpdate.value) {
            var tempSessions = SessionDBService(context).getAllSessions()
            tempSessions = tempSessions.sortedByDescending { it.getDate().timeInMillis }
            sessions.clear()
            sessions.addAll(tempSessions)
            MainActivity.historyDataUpdate.value = false
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            SessionsHistoryList()
        }
    }

    @Composable
    fun SessionsHistoryList() {
        LazyColumn {
            items(sessions.size) {
                SessionItem(sessions[it])
            }
        }
    }

    @Composable
    fun SessionItem(session: Session) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 10.dp)
                .fillMaxWidth()
                .clickable {
                    sessionDetailsPopupVisible.value = !sessionDetailsPopupVisible.value
                    popupSessionID = session.id
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SessionTagsList(session.tags)
            Text(text = durationInSecondsToHoursAndMinutes(session.getDuration()), fontSize = 20.sp)
        }
        if (sessionDetailsPopupVisible.value && popupSessionID == session.id) {
            SessionDetailsPopup(context, session).GeneratePopup(sessionDetailsPopupVisible)
        }
    }

    @Composable
    fun SessionTagsList(tags: MutableList<Tag>) {
        var tagsString = ""
        tags.forEach {
            tagsString += it.tagName + " "
        }
        Text(
            text = tagsString,
            fontSize = 20.sp,
            modifier = Modifier
                .widthIn(max = 300.dp, min = 0.dp)
                .padding(start = 10.dp, end = 10.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}