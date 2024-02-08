package com.ozarskiapps.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.ozarskiapps.scoreboard.MINUTES_IN_HOUR
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.SECONDS_IN_MINUTE
import com.ozarskiapps.scoreboard.Tag
import com.ozarskiapps.scoreboard.database.SessionDBService
import com.ozarskiapps.scoreboard.database.TagDBService
import com.ozarskiapps.scoreboard.session.Session
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.onTertiaryContainerDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import org.apache.commons.lang3.tuple.MutablePair
import java.util.Calendar
import kotlin.concurrent.thread

class AddSessionPopup(val context: Context) : ComponentActivity() {

    private lateinit var popupVisible: MutableState<Boolean>
    private lateinit var tagListPick: SnapshotStateList<MutablePair<Tag, Boolean>>
    private lateinit var hourPickerValue: MutableState<Hours>

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        this.popupVisible = popupVisible
        Popup(
            onDismissRequest = {
                popupVisible.value = false
                com.ozarskiapps.scoreboard.MainActivity.totalDurationUpdate.value = true
                com.ozarskiapps.scoreboard.MainActivity.tagsListUpdate.value = true
            },
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            properties = PopupProperties(focusable = true)
        ) {
            AddSessionPopupLayout()
        }
    }

    @Composable
    private fun AddSessionPopupLayout() {

        GenericPopupContent.GenerateContent(
            widthMin = 0,
            widthMax = 375,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DurationLabel()
            HourPicker24hMax()

            TagListHeader()
            TagPickList()

            AddSessionButton()
        }

    }

    @Composable
    private fun DurationLabel() {
        Text(
            text = context.getString(R.string.add_session_popup_duration_header),
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 10.dp, start = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = Typography.titleLarge,
            color = onPrimaryDark
        )
    }

    @Composable
    private fun AddSessionButton() {
        Button(
            onClick = {
                thread {
                    addNewSession()
                    popupVisible.value = false
                }
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = onPrimaryDark),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(
                text = context.getString(R.string.simple_add_button_text),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge
            )
        }
    }


    private fun addNewSession() {
        val selectedTags = tagListPick.filter { it.right }.map { it.left }
        val durationMinutes =
            hourPickerValue.value.hours * com.ozarskiapps.scoreboard.MINUTES_IN_HOUR + hourPickerValue.value.minutes
        val durationSeconds = durationMinutes * com.ozarskiapps.scoreboard.SECONDS_IN_MINUTE
        val sessionDate = Calendar.getInstance()
        val session = Session(
            durationSeconds.toLong(),
            sessionDate,
            -1,
            selectedTags.toMutableList()
        )
        SessionDBService(context).addSession(session)
        com.ozarskiapps.scoreboard.MainActivity.totalDurationUpdate.value = true
        com.ozarskiapps.scoreboard.MainActivity.sessionsListUpdate.value = true
        com.ozarskiapps.scoreboard.MainActivity.tagsListUpdate.value = true
    }

    @Composable
    private fun TagListHeader() {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = context.getString(R.string.add_session_popup_tag_list_header),
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, start = 20.dp),
                textAlign = TextAlign.Start,
                style = Typography.titleLarge,
                color = onPrimaryDark
            )
            AddNewTag()
        }
    }

    @Composable
    private fun HourPicker24hMax() {
        hourPickerValue = remember { mutableStateOf(FullHours(0, 0)) }
        HoursNumberPicker(
            value = hourPickerValue.value,
            leadingZero = true,
            onValueChange = { hourPickerValue.value = it },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .width(200.dp),
            hoursDivider = {
                Text(
                    modifier = Modifier.padding(start = 8.dp, end = 20.dp),
                    textAlign = TextAlign.Center,
                    text = "h",
                    style = Typography.labelLarge
                )
            },
            minutesDivider = {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    textAlign = TextAlign.Center,
                    text = "m",
                    style = Typography.labelLarge
                )
            },
            textStyle = Typography.labelLarge,
            dividersColor = onPrimaryDark
        )
    }

    @Composable
    private fun TagPickList() {
        tagListPick = remember {
            mutableStateListOf()
        }
        //necessary to avoid duplicating the whole list due to recomposition when adding a new tag
        if(tagListPick.isEmpty()){
            tagListPick.addAll(TagDBService(context).getAllTags().map { MutablePair(it, false) })
            tagListPick.sortBy{ it.left.tagName.lowercase() }
        }
        LazyColumn(
            modifier = Modifier
                .height(300.dp)
                .width(375.dp)
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .border(
                    width = 1.dp,
                    color = onPrimaryDark,
                    shape = RoundedCornerShape(25.dp)
                )
        ) {
            items(tagListPick.size) { index ->
                val tagPicked = remember { mutableStateOf(tagListPick[index].right) }
                val tag = tagListPick[index].left
                TagPickListItem(tag, tagPicked, tagListPick[index])
            }
        }
    }

    @Composable
    private fun TagPickListItem(
        tag: Tag,
        tagPicked: MutableState<Boolean>,
        item: MutablePair<Tag, Boolean>
    ) {
        val contentColor: Color = if (tagPicked.value) {
            onPrimaryDark
        } else {
            onTertiaryContainerDark
        }

        val iconResource = if (tagPicked.value) {
            painterResource(R.drawable.baseline_label_24)
        } else {
            painterResource(R.drawable.outline_label_24)
        }

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 20.dp)
                .clickable {
                    tagPicked.value = !tagPicked.value
                    item.right = tagPicked.value
                }
        ) {
            Icon(
                painter = iconResource,
                contentDescription = "Tag icon",
                tint = contentColor,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = tag.tagName,
                fontSize = 20.sp,
                color = contentColor,
                modifier = Modifier
                    .padding(start = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    @Composable
    private fun AddNewTag() {
        val dialogOpen = remember { mutableStateOf(false) }
        AddNewTagButton(dialogOpen)
        if (dialogOpen.value) {
            AddTagDialog(dialogOpen, context, tagListPick).GenerateDialog()
        }
    }

    @Composable
    private fun AddNewTagButton(dialogOpen: MutableState<Boolean>) {
        FloatingActionButton(
            onClick = {
                dialogOpen.value = true
            },
            shape = RoundedCornerShape(50.dp),
            backgroundColor = onPrimaryDark,
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
            modifier = Modifier.height(25.dp).padding(end=20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.simple_add_button_text),
                    fontSize = 12.sp,
                    color = primaryDark,
                    modifier = Modifier.padding(start = 3.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add button",
                    tint = primaryDark,
                    modifier = Modifier.size(12.dp)
                )
            }

        }
    }
}

@Preview
@Composable
fun AddSessionPopupPreview() {
    val popupVisible = remember { mutableStateOf(true) }
    AddSessionPopup(context = LocalContext.current).GeneratePopup(popupVisible)
}