package com.example.scoreboard.popups

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.scoreboard.R
import com.example.scoreboard.Tag
import com.example.scoreboard.database.TagDBService
import org.apache.commons.lang3.tuple.MutablePair
import kotlin.concurrent.thread

class AddTagDialog(
    private val dialogOpen: MutableState<Boolean>,
    val context: Context,
    private val tagListPick: MutableList<MutablePair<Tag, Boolean>>
) {

    private var tagName = ""

    @Composable
    fun GenerateDialog() {
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
                val newTagName = remember { mutableStateOf("") }
                TagNameField(newTagName)
                AddButton()
            }
        }
    }

    @Composable
    fun TagNameField(name: MutableState<String>) {
        OutlinedTextField(
            value = name.value,
            onValueChange = {
                name.value = it
                tagName = it
            },
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
    }

    @Composable
    fun AddButton() {
        Button(
            onClick = {
                addTag()
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

    private fun addTag() {
        thread {
            if (tagName != "") {
                val newTag = Tag(tagName = tagName, id = -1)
                newTag.id = TagDBService(context).addTag(newTag)
                tagListPick.add(MutablePair(newTag, false))
            }
            else{
                Toast.makeText(context, "Tag name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialogOpen.value = false
        }
    }
}