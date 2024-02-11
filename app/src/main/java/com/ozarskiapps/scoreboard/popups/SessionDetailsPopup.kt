package com.ozarskiapps.scoreboard.popups

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.base.Tag
import com.example.base.session.Session
import com.ozarskiapps.global.durationInSecondsToHoursAndMinutes
import com.ozarskiapps.global.formatDate
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.database.SessionDBService
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.errorContainerDark
import com.ozarskiapps.scoreboard.ui.theme.errorDark
import com.ozarskiapps.scoreboard.ui.theme.onErrorDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryContainerDark

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
            elevation = ButtonDefaults.buttonElevation(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = errorDark)
        ) {
            Text(
                text = context.getString(R.string.simple_delete_button_text),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = onErrorDark,
                style = Typography.titleLarge
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
            MainActivity.sessionsListUpdate.value = true
            MainActivity.totalDurationUpdate.value = true
            MainActivity.tagsListUpdate.value = true
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
                tint = primaryContainerDark,
                modifier = Modifier.size(30.dp)
            )
            val formattedDate = formatDate(session.getDate())
            Text(
                text = formattedDate,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 5.dp),
                style = Typography.labelLarge
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
                tint = errorContainerDark,
                modifier = Modifier.size(30.dp)
            )
            val durationString =
                durationInSecondsToHoursAndMinutes(session.getDuration())
            Text(
                text = durationString,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 5.dp),
                style = Typography.labelLarge
            )
        }
    }

    @Composable
    private fun SessionTagsList() {
        session.tags.sortBy { it.tagName.lowercase() }
        Text(
            text = context.getString(R.string.session_details_tag_list_label),
            fontSize = 19.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
            textAlign = TextAlign.Start,
            style = Typography.titleLarge,
            color = onPrimaryDark
        )
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 0.dp, max = 300.dp)
                .padding(start = 20.dp, end = 20.dp)
                .border(
                    width = 1.dp,
                    color = onPrimaryDark,
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
                tint = onPrimaryDark,
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
                overflow = TextOverflow.Ellipsis,
                style = Typography.titleMedium
            )
        }
    }
}