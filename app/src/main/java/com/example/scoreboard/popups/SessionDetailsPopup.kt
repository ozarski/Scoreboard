package com.example.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.window.PopupProperties
import com.example.scoreboard.MainActivity
import com.example.scoreboard.Tag
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.durationInSecondsToHoursAndMinutes
import com.example.scoreboard.formatDate
import com.example.scoreboard.session.Session

class SessionDetailsPopup(val context: Context, val session: Session) : ComponentActivity() {

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = { popupVisible.value = false },
            properties = PopupProperties(focusable = false, dismissOnClickOutside = false)
        ) {
            SessionDetailsPopupLayout(popupVisible)
        }
    }

    @Composable
    fun SessionDetailsPopupLayout(popupVisible: MutableState<Boolean>) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .heightIn(min = 0.dp, max = 500.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SessionDate()
            SessionDuration()
            SessionTagsList()
            DeleteButton(popupVisible)
        }
    }

    @Composable
    fun DeleteButton(popupVisible: MutableState<Boolean>) {
        Button(
            onClick = {
                SessionDBService(context).deleteSessionByID(session.id)
                popupVisible.value = false
                MainActivity.historyDataUpdate.value = true
                MainActivity.activitiesDataUpdate.value = true
            },
            modifier = Modifier.padding(10.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.elevation(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
        ) {
            Text(
                text = "Delete",
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun SessionDate() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Date:",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            val formattedDate = formatDate(session.getDate())
            Text(
                text = formattedDate,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun SessionDuration() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Duration:",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            val durationString = durationInSecondsToHoursAndMinutes(session.getDuration())
            Text(
                text = durationString,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun SessionTagsList() {
        Text(
            text = "Tags:",
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            textAlign = TextAlign.Start
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 300.dp)
                .padding(start = 30.dp, end = 30.dp, bottom = 10.dp)
        ) {
            items(session.tags.size) {
                TagItem(session.tags[it])
            }
        }
    }

    @Composable
    fun TagItem(tag: Tag) {
        Row(modifier = Modifier.padding(5.dp)){
            Text(
                text = tag.tagName,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(android.graphics.Color.parseColor("#dedede")),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 2.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}