package com.example.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import com.example.scoreboard.MainActivity
import com.example.scoreboard.Tag
import com.example.scoreboard.database.TagDBService

class TagDetailsPopup(val context: Context, val tag: Tag) : ComponentActivity() {

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = { popupVisible.value = false },
        ) {
            TagDetailsPopupLayout(popupVisible)
        }
    }

    @Composable
    fun TagDetailsPopupLayout(popupVisible: MutableState<Boolean>) {
        Column(
            modifier = Modifier
                .width(300.dp)
                .height(300.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            TagName()
            ChangeNameButton()
            DeleteButton(popupVisible)
        }
    }

    @Composable
    fun TagName() {
        Text(text = "Tag name", fontSize = 25.sp, modifier = Modifier.padding(10.dp))
        Text(text = tag.tagName, fontSize = 20.sp, modifier = Modifier.padding(bottom = 10.dp))
    }

    @Composable
    fun ChangeNameButton() {
        val dialogOpen = remember { mutableStateOf(false) }
        val newTagName = remember { mutableStateOf(tag.tagName) }
        Button(onClick = { dialogOpen.value = true}, modifier = Modifier.padding(bottom = 10.dp)) {
            Text(text = "Change name")
        }
        if(dialogOpen.value){
            Dialog(onDismissRequest = { dialogOpen.value = false }){
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(Color.LightGray, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Change name",
                        fontSize = 25.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                    OutlinedTextField(
                        value = newTagName.value,
                        onValueChange = { newTagName.value = it },
                        label = { Text(text = "Tag name") },
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (newTagName.value != "") {
                                tag.tagName = newTagName.value
                                TagDBService(context).updateTag(tag)
                            }
                            dialogOpen.value = false
                        },
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(text = "Change name")
                    }
                }
            }
        }
    }

    @Composable
    fun DeleteButton(popupVisible: MutableState<Boolean>) {
        Button(onClick = {
            TagDBService(context).deleteTagByID(tag.id)
            popupVisible.value = false
            MainActivity.activitiesDataUpdate.value = true
        }, modifier = Modifier.padding(bottom = 10.dp)) {
            Text(text = "Delete")
        }
    }
}