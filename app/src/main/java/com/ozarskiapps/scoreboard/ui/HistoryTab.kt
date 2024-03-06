package com.ozarskiapps.scoreboard.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.base.Tag
import com.example.base.session.Session
import com.example.database.ScoreboardDatabase
import com.ozarskiapps.global.durationInSecondsToHoursAndMinutes
import com.ozarskiapps.global.formatDate
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.popups.FilterHistoryPopup
import com.ozarskiapps.scoreboard.popups.SessionDetailsPopup
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.backgroundDark
import com.ozarskiapps.scoreboard.ui.theme.errorContainerDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.onTertiaryContainerDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import com.ozarskiapps.scoreboard.ui.theme.secondaryDark
import java.io.File

class HistoryTab(private val context: Context, private val activityContext: Activity) :
    ComponentActivity() {

    private var popupSessionID = 0L

    @Composable
    fun GenerateLayout() {
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
            elevation = CardDefaults.cardElevation(3.dp),
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
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterButton()
                }
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
                text = durationInSecondsToHoursAndMinutes(
                    MainActivity.filteredDuration.longValue
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
            val interactionSource = remember { MutableInteractionSource() }
            ExportDataIcon()
            ImportDataIcon()
            Icon(
                painter = painterResource(id = R.drawable.baseline_filter_list_24),
                contentDescription = "Filter popup button",
                tint = primaryDark,
                modifier = Modifier.filterButtonIconModifier(
                    interactionSource = interactionSource,
                    filterPopupVisible = filterPopupVisible
                )
            )
        }
        if (filterPopupVisible.value) {
            FilterHistoryPopup(context).GeneratePopup(filterPopupVisible)
        }
    }

    @Composable
    fun ImportDataIcon() {
        Icon(
            painter = painterResource(id = R.drawable.baseline_download_24),
            contentDescription = "Filter popup button",
            tint = onPrimaryDark,
            modifier = Modifier
                .clickable {
                    pickFile()
                }
                .size(50.dp)
                .padding(horizontal = 5.dp)
        )
    }

    @Composable
    fun ExportDataIcon(){
        Icon(
            painter = painterResource(id = R.drawable.baseline_publish_24),
            contentDescription = "Filter popup button",
            tint = onPrimaryDark,
            modifier = Modifier
                .clickable {
                    ScoreboardDatabase(context).exportDatabase()
                }
                .size(50.dp)
                .padding(horizontal = 5.dp)
        )
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            //can't filter for .db files so filtering for binary files (which includes .db
            //files but exclude most image, video, audio, txt etc.)
            type = "application/octet-stream"
        }

        activityContext.startActivityForResult(intent, MainActivity.PICK_DB_REQUEST_CODE)
    }

    private fun Modifier.filterButtonIconModifier(
        interactionSource: MutableInteractionSource,
        filterPopupVisible: MutableState<Boolean>
    ): Modifier = composed {
        this
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
                interactionSource = interactionSource,
                onClick = {
                    filterPopupVisible.value = !filterPopupVisible.value
                }
            )
            .padding(5.dp)
    }

    @Composable
    fun SessionsHistoryList() {
        val sessionDetailsPopupVisible = remember { mutableStateOf(false) }

        Card(
            modifier = Modifier.padding(10.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.background(
                    color = primaryDark,
                    shape = RoundedCornerShape(25.dp)
                )
            ) {
                items(MainActivity.sessionList.size) {
                    SessionItem(MainActivity.sessionList[it], sessionDetailsPopupVisible)
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(0.95f),
                        thickness = 0.7.dp,
                        color = onPrimaryDark
                    )
                }
                item {
                    LoadMoreSessionsButton {
                        MainActivity.loadMoreSessions(context)
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
                containerColor = onPrimaryDark
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
            val formattedDate = formatDate(session.getDate())
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
                text = durationInSecondsToHoursAndMinutes(session.getDuration()),
                fontSize = 15.sp,
                style = Typography.labelLarge
            )
        }
    }

    @Composable
    fun SessionTagsText(tags: MutableList<Tag>) {
        val tagsString = tags.sortedBy { it.tagName.lowercase() }.joinToString(" ") { it.tagName }
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