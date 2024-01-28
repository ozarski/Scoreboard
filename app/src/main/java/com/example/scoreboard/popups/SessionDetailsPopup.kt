package com.example.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.scoreboard.MainActivity
import com.example.scoreboard.R
import com.example.scoreboard.Tag
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.durationInSecondsToHoursAndMinutes
import com.example.scoreboard.formatDate
import com.example.scoreboard.session.Session

class SessionDetailsPopup(val context: Context, val session: Session) : ComponentActivity() {

    private lateinit var popupVisible: MutableState<Boolean>

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        this.popupVisible = popupVisible
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = { popupVisible.value = false },
            properties = PopupProperties(focusable = true)
        ) {
            SessionDetailsPopupLayout()
        }
    }

    @Composable
    private fun SessionDetailsPopupLayout() {
        GenericPopupContent.GenerateContent(
            width = 350,
            heightMin = 0,
            heightMax = 500
        ) {
            SessionDate()
            SessionDuration()
            SessionTagsList()
            DeleteButton()
        }
    }

    @Composable
    private fun DeleteButton() {
        val confirmPopupVisible = remember { mutableStateOf(false) }
        val decision = remember { mutableStateOf(false) }
        Button(
            onClick = {
                confirmPopupVisible.value = true
            },
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.elevation(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(context.getColor(R.color.delete_red)))
        ) {
            Text(
                text = context.getString(R.string.simple_delete_button_text),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }

        if (confirmPopupVisible.value) {
            ConfirmPopup(context).GeneratePopup(
                popupVisible = confirmPopupVisible,
                decision = decision,
                otherPopupVisible = popupVisible
            )
        }
        if (decision.value) {
            SessionDBService(context).deleteSessionByID(session.id)
            popupVisible.value = false
            MainActivity.historyDataUpdate.value = true
            MainActivity.sessionsDataUpdate.value = true
            MainActivity.tagsDataUpdate.value = true
        }
    }

    @Composable
    private fun SessionDate() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp, start = 20.dp, end = 20.dp, top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_date_range_24),
                contentDescription = "Session details date icon",
                tint = Color(context.getColor(R.color.date_icon_tint)),
                modifier = Modifier.size(30.dp)
            )
            val formattedDate = formatDate(session.getDate())
            Text(
                text = formattedDate,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }

    @Composable
    private fun SessionDuration() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_access_time_24),
                contentDescription = "Session details duration icon",
                tint = Color(context.getColor(R.color.duration_icon_tint)),
                modifier = Modifier.size(30.dp)
            )
            val durationString = durationInSecondsToHoursAndMinutes(session.getDuration())
            Text(
                text = durationString,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    }

    @Composable
    private fun SessionTagsList() {
        Text(
            text = context.getString(R.string.session_details_tag_list_label),
            fontSize = 19.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            textAlign = TextAlign.Start
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 300.dp)
                .padding(start = 20.dp, end = 20.dp)
                .border(
                    width = 1.dp,
                    color = Color(context.getColor(R.color.main_ui_buttons_color)),
                    shape = RoundedCornerShape(25.dp)
                )
        ) {
            items(session.tags.size) {
                TagItem(session.tags[it])
            }
        }
    }

    @Composable
    private fun TagItem(tag: Tag) {
        Row(modifier = Modifier.padding(top = 5.dp, start = 15.dp, bottom = 5.dp, end = 15.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_tag_24),
                contentDescription = "Session details tag icon",
                tint = Color.LightGray,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = tag.tagName,
                fontSize = 20.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}