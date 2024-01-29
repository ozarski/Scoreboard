package com.example.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.example.scoreboard.MainActivity
import com.example.scoreboard.R
import com.example.scoreboard.Tag
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.database.TagDBService
import com.example.scoreboard.session.Session
import org.apache.commons.lang3.tuple.MutablePair
import java.util.Calendar

class AddSessionPopup(val context: Context) : ComponentActivity() {

    private lateinit var popupVisible: MutableState<Boolean>
    private lateinit var tagListPick: SnapshotStateList<MutablePair<Tag, Boolean>>
    private lateinit var hourPickerValue: MutableState<Hours>

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        this.popupVisible = popupVisible
        tagListPick = remember {
            SnapshotStateList()
        }
        hourPickerValue = remember { mutableStateOf(FullHours(0, 0)) }
        Popup(
            onDismissRequest = {
                popupVisible.value = false
                MainActivity.totalDurationUpdate.value = true
                MainActivity.tagsListUpdate.value = true
            },
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            properties = PopupProperties(focusable = true)
        ) {
            AddSessionPopupLayout()
        }
    }

    @Composable
    private fun AddSessionPopupLayout() {
        val tagList = TagDBService(context).getAllTags()
        tagListPick.addAll(tagList.map { MutablePair(it, false) })

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
            fontSize = 19.sp,
            modifier = Modifier
                .padding(top = 10.dp, start = 20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }

    @Composable
    private fun AddSessionButton() {

        Button(
            onClick = {
                addNewSession()
            },
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(context.getColor(R.color.main_ui_buttons_color))),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(
                text = context.getString(R.string.simple_add_button_text),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }


    private fun addNewSession() {

        val selectedTags = tagListPick.filter { it.right }.map { it.left }
        val durationMinutes =
            hourPickerValue.value.hours * 60 + hourPickerValue.value.minutes
        val durationSeconds = durationMinutes * 60
        val sessionDate = Calendar.getInstance()
        val session = Session(
            durationSeconds.toLong(),
            sessionDate,
            -1,
            selectedTags.toMutableList()
        )
        popupVisible.value = false
        MainActivity.totalDurationUpdate.value = true
        MainActivity.sessionsListUpdate.value = true
        MainActivity.tagsListUpdate.value = true
        SessionDBService(context).addSession(session)
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
                fontSize = 19.sp,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp, start = 20.dp),
                textAlign = TextAlign.Start
            )
            AddNewTag()
        }
    }

    @Composable
    private fun HourPicker24hMax() {
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
                    text = "h"
                )
            },
            minutesDivider = {
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    textAlign = TextAlign.Center,
                    text = "m",
                )
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            dividersColor = Color(context.getColor(R.color.main_ui_buttons_color))
        )
    }

    @Composable
    private fun TagPickList() {
        tagListPick.sortedBy { it.left.tagName }
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .width(375.dp)
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .border(
                    width = 1.dp,
                    color = Color(context.getColor(R.color.main_ui_buttons_color)),
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
        val textColor: Color = if (tagPicked.value) {
            Color(context.getColor(R.color.main_ui_buttons_color))
        } else {
            Color.Black
        }

        val iconColor = if (tagPicked.value) {
            Color(context.getColor(R.color.tag_icon_color))
        } else {
            Color.LightGray
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
                tint = iconColor,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = tag.tagName,
                fontSize = 20.sp,
                color = textColor,
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
            AddNewTagDialog(dialogOpen)
        }
    }

    @Composable
    private fun AddNewTagButton(dialogOpen: MutableState<Boolean>) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 20.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    dialogOpen.value = true
                },
                shape = RoundedCornerShape(50.dp),
                backgroundColor = Color(context.getColor(R.color.main_ui_buttons_color)),
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                modifier = Modifier.height(25.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ADD",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(start = 3.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add button",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }

            }
        }
    }

    @Composable
    private fun AddNewTagDialog(dialogOpen: MutableState<Boolean>) {

        val newTagName = remember { mutableStateOf("") }
        Dialog(onDismissRequest = { dialogOpen.value = false }) {
            Column(
                modifier = Modifier
                    .width(280.dp)
                    .background(Color.White, RoundedCornerShape(25.dp))
                    .border(
                        width = 2.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(25.dp)
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = newTagName.value,
                    onValueChange = { newTagName.value = it },
                    label = { Text(text = context.getString(R.string.add_new_tag_dialog_tag_name_label)) },
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color(context.getColor(R.color.main_ui_buttons_color)),
                        unfocusedIndicatorColor = Color(context.getColor(R.color.main_ui_buttons_color)),
                        cursorColor = Color(context.getColor(R.color.main_ui_buttons_color)),
                        textColor = Color.Black,
                        focusedLabelColor = Color(context.getColor(R.color.main_ui_buttons_color)),
                        unfocusedLabelColor = Color(context.getColor(R.color.main_ui_buttons_color))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                Button(
                    onClick = {
                        if (newTagName.value != "") {
                            val newTag = Tag(tagName = newTagName.value, id = -1)
                            newTag.id = TagDBService(context).addTag(newTag)
                            tagListPick.add(MutablePair(newTag, false))
                            newTagName.value = ""
                        }
                        dialogOpen.value = false
                    },
                    modifier = Modifier
                        .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(context.getColor(R.color.main_ui_buttons_color))),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Text(
                        text = context.getString(R.string.simple_add_button_text),
                        color = Color.White
                    )
                }
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
