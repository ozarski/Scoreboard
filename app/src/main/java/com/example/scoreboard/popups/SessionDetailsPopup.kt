package com.example.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.scoreboard.MainActivity
import com.example.scoreboard.Tag
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.durationInSecondsToHoursAndMinutes
import com.example.scoreboard.session.Session

class SessionDetailsPopup(val context: Context, val session: Session) : ComponentActivity() {

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = { popupVisible.value = false },
        ) {
            SessionDetailsPopupLayout(popupVisible)
        }
    }

    @Composable
    fun SessionDetailsPopupLayout(popupVisible: MutableState<Boolean>) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SessionDuration()
            SessionTagsList()
            DeleteButton(popupVisible)
        }
    }

    @Composable
    fun DeleteButton(popupVisible: MutableState<Boolean>) {
        Button(onClick = {
            SessionDBService(context).deleteSessionByID(session.id)
            popupVisible.value = false
            MainActivity.historyDataUpdate.value = true
            MainActivity.activitiesDataUpdate.value = true
        }) {
            Text(
                text = "Delete",
                fontSize = 20.sp,
                modifier = Modifier.width(200.dp),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun SessionDuration() {
        Text(
            text = "Duration",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        val durationString = durationInSecondsToHoursAndMinutes(session.getDuration())
        Text(
            text = durationString,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

    @Composable
    fun SessionTagsList() {
        Text(
            text = "Tags",
            fontSize = 25.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        LazyColumn(Modifier.fillMaxWidth().height(100.dp).padding(10.dp)) {
            items(session.tags.size) {
                TagItem(session.tags[it])
            }
        }
    }

    @Composable
    fun TagItem(tag: Tag) {
        Text(
            text = tag.tagName,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}