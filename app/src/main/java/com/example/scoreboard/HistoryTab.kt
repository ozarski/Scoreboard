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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.popups.FilterHistoryPopup
import com.example.scoreboard.popups.SessionDetailsPopup
import com.example.scoreboard.session.Session

class HistoryTab(val context: Context) : ComponentActivity() {

    private lateinit var sessions: SnapshotStateList<Session>
    private lateinit var sessionDetailsPopupVisible: MutableState<Boolean>
    private var popupSessionID = 0L

    @Composable
    fun GenerateLayout() {
        sessions = remember { mutableStateListOf() }
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
            FilterSessionsByTagsRow()
            SessionsHistoryList()
        }
    }

    @Composable
    fun FilterSessionsByTagsRow() {
        val filterPopupVisible = remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            var selectedSessionsDuration = 0L
            sessions.forEach { selectedSessionsDuration += it.getDuration() }
            val selectedSessionsDurationString =
                durationInSecondsToHoursAndMinutes(selectedSessionsDuration)
            Text(
                text = String.format(
                    context.getString(R.string.selected_sessions_duration),
                    selectedSessionsDurationString
                ),
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
            Button(
                onClick = {
                    filterPopupVisible.value = !filterPopupVisible.value
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Filter popup button",
                    tint = Color.Black,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
        if (filterPopupVisible.value) {
            FilterHistoryPopup(context).GeneratePopup(filterPopupVisible, sessions)
        }
    }

    @Composable
    fun SessionsHistoryList() {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(sessions.size) {
                SessionItem(sessions[it])
                Divider(
                    color = Color.LightGray,
                    thickness = 0.7.dp,
                    modifier = Modifier.fillMaxWidth(0.95f)
                )
            }
        }
    }

    @Composable
    fun SessionItem(session: Session) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sessionDetailsPopupVisible.value = !sessionDetailsPopupVisible.value
                    popupSessionID = session.id
                }
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SessionTagsText(session.tags)
            Column(verticalArrangement = Arrangement.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_date_range_24),
                        contentDescription = "Session date icon",
                        tint = Color(context.getColor(R.color.date_icon_tint)),
                        modifier = Modifier.size(20.dp)
                    )
                    val formattedDate = formatDate(session.getDate())
                    Text(
                        text = formattedDate,
                        fontSize = 15.sp,
                        modifier = Modifier.widthIn(max = 275.dp, min = 0.dp),
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_access_time_24),
                        contentDescription = "Session duration icon",
                        tint = Color(context.getColor(R.color.duration_icon_tint)),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = durationInSecondsToHoursAndMinutes(session.getDuration()),
                        fontSize = 15.sp
                    )
                }
            }
        }
        if (sessionDetailsPopupVisible.value && popupSessionID == session.id) {
            SessionDetailsPopup(context, session).GeneratePopup(sessionDetailsPopupVisible)
        }
    }

    @Composable
    fun SessionTagsText(tags: MutableList<Tag>) {
        var tagsString = ""
        tags.forEach {
            tagsString += it.tagName + " "
        }
        Column(verticalArrangement = Arrangement.Center){
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_tag_24),
                    contentDescription = "Session tags icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(25.dp).padding(end = 3.dp)
                )
                Text(
                    text = tagsString,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .widthIn(max = 275.dp, min = 0.dp)
                        .padding(end = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}