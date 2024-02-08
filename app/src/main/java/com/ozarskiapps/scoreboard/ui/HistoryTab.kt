package com.ozarskiapps.scoreboard.ui

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.Tag
import com.ozarskiapps.scoreboard.database.SessionTagDBService
import com.ozarskiapps.scoreboard.database.StatsDBService
import com.ozarskiapps.scoreboard.database.TagDBService
import com.ozarskiapps.scoreboard.durationInSecondsToHoursAndMinutes
import com.ozarskiapps.scoreboard.formatDate
import com.ozarskiapps.scoreboard.popups.FilterHistoryPopup
import com.ozarskiapps.scoreboard.popups.SessionDetailsPopup
import com.ozarskiapps.scoreboard.session.Session
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.backgroundDark
import com.ozarskiapps.scoreboard.ui.theme.errorContainerDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.onTertiaryContainerDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import com.ozarskiapps.scoreboard.ui.theme.secondaryDark
import org.apache.commons.lang3.tuple.MutablePair

class HistoryTab(val context: Context) : ComponentActivity() {

    private var popupSessionID = 0L
    private lateinit var tagListPick: SnapshotStateList<MutablePair<MutableState<Tag>, MutableState<Boolean>>>
    private lateinit var sessionsDuration: MutableState<Long>
    private var page: Int = 1
    private var pageSize: Int = 15

    @Composable
    fun GenerateLayout() {
        tagListPick = remember { mutableStateListOf() }
        reloadTags()
        HistoryTabLayout()
    }

    @Composable
    fun HistoryTabLayout() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundDark),
            verticalArrangement = Arrangement.Top
        ) {
            FilterSessionsHeader()
            SessionsHeader()
            SessionsHistoryList()
        }
    }

    @Composable
    fun FilterSessionsHeader() {
        Card(
            modifier = Modifier.padding(10.dp),
            elevation = 3.dp,
            shape = RoundedCornerShape(25.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = primaryDark, shape = RoundedCornerShape(25.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Duration()
                FilterButton()
            }
        }
    }

    @Composable
    fun SessionsHeader() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Sessions",
                fontSize = 25.sp,
                modifier = Modifier.padding(start = 10.dp),
                style = Typography.bodyLarge,
                color = onTertiaryContainerDark
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_history_edu_24),
                contentDescription = "Activities icon",
                modifier = Modifier
                    .size(30.dp)
                    .padding(start = 3.dp),
                tint = onTertiaryContainerDark
            )
        }
    }

    @Composable
    fun Duration() {
        sessionsDuration = remember { mutableStateOf(0L) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_access_time_24),
                contentDescription = "Sessions duration icon",
                tint = onPrimaryDark,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 10.dp, end = 3.dp)
            )
            Text(
                text = com.ozarskiapps.scoreboard.durationInSecondsToHoursAndMinutes(
                    sessionsDuration.value
                ),
                fontSize = 25.sp,
                style = Typography.labelLarge
            )
        }
    }

    @Composable
    fun FilterButton() {
        val filterPopupVisible = remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_filter_list_24),
                contentDescription = "Filter popup button",
                tint = primaryDark,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .padding(vertical = 5.dp)
                    .size(40.dp)
                    .background(
                        shape = CircleShape,
                        color = onPrimaryDark
                    )
                    .clickable(
                        indication = rememberRipple(
                            bounded = false,
                            color = onTertiaryContainerDark,
                            radius = 40.dp
                        ),
                        interactionSource = MutableInteractionSource(),
                        onClick = {
                            filterPopupVisible.value = !filterPopupVisible.value
                        })
                    .padding(5.dp)
            )
        }
        if (filterPopupVisible.value) {
            FilterHistoryPopup(context).GeneratePopup(filterPopupVisible, tagListPick)
        }
    }

    @Composable
    fun SessionsHistoryList() {
        val sessions = remember { mutableStateListOf<Session>() }
        if (com.ozarskiapps.scoreboard.MainActivity.sessionsListUpdate.value) {
            updateSessions(sessions)
        }
        val sessionDetailsPopupVisible = remember { mutableStateOf(false) }

        Card(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
            elevation = 3.dp,
            shape = RoundedCornerShape(25.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(
                    color = primaryDark,
                    shape = RoundedCornerShape(25.dp)
                )
            ) {
                items(sessions.size) {
                    SessionItem(sessions[it], sessionDetailsPopupVisible)
                    Divider(
                        color = onPrimaryDark,
                        thickness = 0.7.dp,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }
                item {
                    LoadMoreSessionsButton {
                        loadMoreSessions(sessions)
                    }
                }
            }
        }
    }

    @Composable
    fun LoadMoreSessionsButton(loadRunnable: () -> Unit) {
        Button(
            onClick = {
                loadRunnable()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = onPrimaryDark
            )
        ) {
            Text(
                text = "Load more",
                fontSize = 20.sp,
                style = Typography.titleLarge,
                color = primaryDark
            )
        }
    }

    private fun updateSessions(sessions: SnapshotStateList<Session>) {
        page = 1
        println("page number is $page")
        val pickedIDs = tagListPick.filter { it.right.value }.map { it.left.value.id }
        var tempSessions =
            SessionTagDBService(context).getSessionsForTagIDs(pickedIDs, page, pageSize)
        print(tempSessions.size)
        page++
        tempSessions = tempSessions.sortedByDescending { it.getDate().timeInMillis }
        sessions.clear()
        sessions.addAll(tempSessions)
        sessionsDuration.value = StatsDBService(context).getDurationForSessionsWithTags(pickedIDs)
        com.ozarskiapps.scoreboard.MainActivity.sessionsListUpdate.value = false
    }

    private fun loadMoreSessions(sessions: SnapshotStateList<Session>) {
        val pickedIDs = tagListPick.filter { it.right.value }.map { it.left.value.id }
        var tempSessions =
            SessionTagDBService(context).getSessionsForTagIDs(pickedIDs, page, pageSize)
        if (tempSessions.isEmpty()) {
            Toast.makeText(context, "All sessions loaded", Toast.LENGTH_SHORT).show()
            return
        }
        page++
        tempSessions = tempSessions.sortedByDescending { it.getDate().timeInMillis }
        sessions.addAll(tempSessions)
        sessionsDuration.value = StatsDBService(context).getDurationForSessionsWithTags(pickedIDs)
    }

    private fun reloadTags() {
        val tags = TagDBService(context).getAllTags()
        tagListPick.clear()
        tagListPick.addAll(tags.map { MutablePair(mutableStateOf(it), mutableStateOf(false)) })
    }

    @Composable
    fun SessionItem(session: Session, popupVisible: MutableState<Boolean>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    popupVisible.value = !popupVisible.value
                    popupSessionID = session.id
                }
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SessionTagsText(session.tags)
            Column(verticalArrangement = Arrangement.Center) {
                SessionDateRow(session = session)
                SessionDurationRow(session = session)
            }
        }
        if (popupVisible.value && popupSessionID == session.id) {
            SessionDetailsPopup(context, session).GeneratePopup(popupVisible)
        }
    }

    @Composable
    fun SessionDateRow(session: Session) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_date_range_24),
                contentDescription = "Session date icon",
                tint = secondaryDark,
                modifier = Modifier.size(20.dp)
            )
            val formattedDate = com.ozarskiapps.scoreboard.formatDate(session.getDate())
            Text(
                text = formattedDate,
                fontSize = 15.sp,
                modifier = Modifier.widthIn(max = 275.dp, min = 0.dp),
                overflow = TextOverflow.Ellipsis,
                style = Typography.labelLarge
            )
        }
    }

    @Composable
    fun SessionDurationRow(session: Session) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_access_time_24),
                contentDescription = "Session duration icon",
                tint = errorContainerDark,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = com.ozarskiapps.scoreboard.durationInSecondsToHoursAndMinutes(session.getDuration()),
                fontSize = 15.sp,
                style = Typography.labelLarge
            )
        }
    }

    @Composable
    fun SessionTagsText(tags: MutableList<Tag>) {
        var tagsString = ""
        tags.sortedBy { it.tagName.lowercase() }.forEach {
            tagsString += it.tagName + " "
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_tag_24),
                contentDescription = "Session tags icon",
                tint = onPrimaryDark,
                modifier = Modifier
                    .size(25.dp)
                    .padding(end = 3.dp)
            )
            Text(
                text = tagsString,
                fontSize = 18.sp,
                modifier = Modifier
                    .widthIn(max = 260.dp, min = 0.dp)
                    .padding(end = 10.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = Typography.labelLarge
            )
        }
    }
}