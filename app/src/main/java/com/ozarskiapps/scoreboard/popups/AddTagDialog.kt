package com.ozarskiapps.scoreboard.popups

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.base.Tag
import com.example.database.TagDBService
import com.ozarskiapps.global.textFieldColors
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.errorDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import org.apache.commons.lang3.tuple.MutablePair
import kotlin.concurrent.thread

class AddTagDialog(
    private val dialogOpen: MutableState<Boolean>,
    private val context: Context,
    private val tagListPick: MutableList<MutablePair<Tag, Boolean>>
) {

    private var tagName = ""

    @Composable
    fun GenerateDialog() {
        Dialog(onDismissRequest = { dialogOpen.value = false }) {
            GenericPopupContent.GenerateContent(width = 200) {
                TagNameField()
                AddButton()
            }
        }
    }

    @Composable
    fun TagNameField() {
        var name by remember { mutableStateOf("") }
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                tagName = it
            },
            label = { Text(text = context.getString(R.string.add_new_tag_dialog_tag_name_label)) },
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .fillMaxWidth(),
            colors = textFieldColors(
                primaryDark = primaryDark,
                onPrimaryDark = onPrimaryDark,
                errorDark = errorDark
            ),
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(color = onPrimaryDark, fontSize = 16.sp)
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
            colors = ButtonDefaults.buttonColors(containerColor = onPrimaryDark),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(
                text = context.getString(R.string.simple_add_button_text),
                style = Typography.titleLarge
            )
        }
    }

    private fun addTag() {
        thread {
            if (tagName != "") {
                Tag(tagName = tagName, id = -1).run {
                    id = TagDBService(context).addTag(this)
                    tagListPick.add(MutablePair(this, false))
                }
            } else {
                Toast.makeText(context, "Tag name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialogOpen.value = false
        }
    }
}