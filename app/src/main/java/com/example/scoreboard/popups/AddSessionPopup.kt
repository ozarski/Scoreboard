package com.example.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.example.scoreboard.MainActivity
import com.example.scoreboard.Tag
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.database.TagDBService
import com.example.scoreboard.session.Session
import org.apache.commons.lang3.tuple.MutablePair
import java.util.Calendar

class AddSessionPopup(val context: Context) : ComponentActivity() {

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        AddSessionPopupLayout(popupVisible = popupVisible)
    }

    @Composable
    fun AddSessionPopupLayout(
        popupVisible: MutableState<Boolean>
    ) {
        val hourPickerValue = remember { mutableStateOf<Hours>(FullHours(0, 0)) }
        val tagList = TagDBService(context).getAllTags()
        val tagListPick = remember { SnapshotStateList<MutablePair<Tag, Boolean>>() }
        tagListPick.addAll(tagList.map { MutablePair(it, false) })
        Popup(
            onDismissRequest = {
                popupVisible.value = false
                MainActivity.activitiesDataUpdate.value = true
            },
            popupPositionProvider = WindowCenterOffsetPositionProvider()
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp, min = 0.dp)
                    .background(Color.LightGray, RoundedCornerShape(16.dp)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Add session", fontSize = 25.sp, modifier = Modifier.padding(10.dp))
                Text(text = "Duration", fontSize = 22.sp, modifier = Modifier.padding(10.dp))
                HourPicker24hMax(hourPickerValue)
                Text(text = "Tags", fontSize = 22.sp, modifier = Modifier.padding(10.dp))
                TagPickList(tagListPick)
                AddNewTag(tagList = tagListPick)
                Button(
                    onClick = {
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
                        MainActivity.activitiesDataUpdate.value = true
                        MainActivity.historyDataUpdate.value = true
                        SessionDBService(context).addSession(session)
                    },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(text = "Add session", fontSize = 22.sp, modifier = Modifier.padding(10.dp))
                }
            }
        }
    }

    @Composable
    fun HourPicker24hMax(hourPickerValue: MutableState<Hours>) {
        HoursNumberPicker(
            value = hourPickerValue.value,
            leadingZero = true,
            onValueChange = { hourPickerValue.value = it },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .width(200.dp),
            hoursDivider = {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    text = "h"
                )
            },
            minutesDivider = {
                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center,
                    text = "m"
                )
            }
        )
    }

    @Composable
    fun TagPickList(tagList: SnapshotStateList<MutablePair<Tag, Boolean>>) {
        tagList.sortedBy { it.left.tagName }
        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .width(375.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            items(tagList.size) { index ->
                val tagPicked = remember { mutableStateOf(tagList[index].right) }
                val tag = tagList[index].left
                TagPickListItem(tag, tagPicked, tagList[index])
            }
        }
    }

    @Composable
    fun TagPickListItem(
        tag: Tag,
        tagPicked: MutableState<Boolean>,
        item: MutablePair<Tag, Boolean>
    ) {
        var textColor = Color.Black
        if (tagPicked.value) {
            textColor = Color.Green
        } else {
            textColor = Color.Black
        }
        Text(
            text = tag.tagName,
            fontSize = 20.sp,
            color = textColor,
            modifier = Modifier
                .padding(vertical = 3.dp, horizontal = 10.dp)
                .clickable {
                    tagPicked.value = !tagPicked.value
                    item.right = tagPicked.value
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    @Composable
    fun AddNewTag(tagList: SnapshotStateList<MutablePair<Tag, Boolean>>) {
        val newTagName = remember { mutableStateOf("") }
        val dialogOpen = remember { mutableStateOf(false) }
        Text(
            text = "Add new tag",
            fontSize = 21.sp,
            modifier = Modifier
                .padding(top = 10.dp)
                .clickable {
                    dialogOpen.value = true
                })
        if (dialogOpen.value) {
            Dialog(onDismissRequest = { dialogOpen.value = false }) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .background(Color.LightGray, RoundedCornerShape(16.dp)),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Add new tag",
                        fontSize = 25.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    OutlinedTextField(
                        value = newTagName.value,
                        onValueChange = { newTagName.value = it },
                        label = { Text(text = "Tag name") },
                        modifier = Modifier
                            .padding(10.dp)
                            .width(250.dp)
                    )
                    Button(
                        onClick = {
                            if (newTagName.value != "") {
                                val newTag = Tag(tagName = newTagName.value, id = -1)
                                newTag.id = TagDBService(context).addTag(newTag)
                                tagList.add(MutablePair(newTag, false))
                                newTagName.value = ""
                            }
                            dialogOpen.value = false
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(text = "Add")
                    }
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
