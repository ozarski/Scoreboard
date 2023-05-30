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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
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
            onDismissRequest = { popupVisible.value = false },
            popupPositionProvider = WindowCenterOffsetPositionProvider()
        ) {
            Column(
                modifier = Modifier
                    .width(280.dp)
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
                Button(onClick = {
                    val selectedTags = tagListPick.filter { it.right }.map { it.left }
                    val durationMinutes = hourPickerValue.value.hours * 60 + hourPickerValue.value.minutes
                    val durationSeconds = durationMinutes * 60
                    val sessionDate = Calendar.getInstance()
                    val session = Session(
                        durationSeconds.toLong(),
                        sessionDate,
                        -1,
                        selectedTags.toMutableList()
                    )
                    popupVisible.value = false
                    SessionDBService(context).addSession(session)
                },
                modifier = Modifier.padding(10.dp)){
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
                .width(250.dp)
                .padding(top = 10.dp, bottom = 10.dp)
        ) {
            items(tagList.size) { index ->
                TagPickListItem(tagList[index])
            }
        }
    }

    @Composable
    fun TagPickListItem(item: MutablePair<Tag, Boolean>) {
        val textColor = remember { mutableStateOf(Color.Black) }
        Text(
            text = item.left.tagName,
            fontSize = 20.sp,
            color = textColor.value,
            modifier = Modifier
                .padding(top = 6.dp)
                .clickable {
                    item.right = !item.right
                    if (textColor.value == Color.Black) {
                        textColor.value = Color.Green
                    } else {
                        textColor.value = Color.Black
                    }
                },
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
            Dialog(onDismissRequest = { dialogOpen.value = false }){
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
