package com.ozarskiapps.scoreboard.popups

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.base.Tag
import com.example.database.TagDBService
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.errorDark
import com.ozarskiapps.scoreboard.ui.theme.onErrorDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark

class TagDetailsPopup(val context: Context, val tag: Tag) : ComponentActivity() {

    private lateinit var popupVisible: MutableState<Boolean>

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        this.popupVisible = popupVisible
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = { popupVisible.value = false },
            properties = PopupProperties(focusable = true)
        ) {
            TagDetailsPopupLayout()
        }
    }

    @Composable
    private fun TagDetailsPopupLayout() {
        GenericPopupContent.GenerateContent(
            widthMin = 200,
            widthMax = 300,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){

            NameHeader()
            TagName()
            val buttonsModifier = Modifier
                .padding(horizontal = 10.dp)
                .widthIn(100.dp)
                .weight(1f)
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ChangeNameButton(buttonsModifier)
                DeleteButton(buttonsModifier)
            }
        }
    }

    @Composable
    private fun NameHeader() {
        Text(
            text = context.getString(R.string.tag_details_name_header),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 10.dp),
            style = Typography.labelLarge
        )
    }

    @Composable
    private fun TagName() {
        Text(
            text = tag.tagName,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp),
            textAlign = TextAlign.Center,
            style = Typography.labelLarge
        )
    }

    @Composable
    private fun ChangeNameButton(modifier: Modifier) {
        val dialogOpen = remember { mutableStateOf(false) }
        val newTagName = remember { mutableStateOf(tag.tagName) }
        Button(
            onClick = { dialogOpen.value = true }, modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = onPrimaryDark),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = context.getString(R.string.simple_rename_button_text), color = primaryDark, style = Typography.titleLarge)
        }
        if (dialogOpen.value) {
            ChangeNameDialog(dialogOpen, newTagName)
        }
    }

    @Composable
    private fun ChangeNameDialog(
        dialogOpen: MutableState<Boolean>,
        newTagName: MutableState<String>
    ) {
        Dialog(onDismissRequest = { dialogOpen.value = false }) {
            GenericPopupContent.GenerateContent(
                width = 300,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = newTagName.value,
                    onValueChange = { newTagName.value = it },
                    label = { Text(text = context.getString(R.string.add_new_tag_dialog_tag_name_label)) },
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = primaryDark,
                        unfocusedContainerColor = primaryDark,
                        cursorColor = onPrimaryDark,
                        errorCursorColor = errorDark,
                        focusedLabelColor = onPrimaryDark,
                        unfocusedLabelColor = onPrimaryDark,
                        focusedTextColor = onPrimaryDark,
                        unfocusedTextColor = onPrimaryDark,
                        focusedBorderColor = onPrimaryDark,
                        unfocusedBorderColor = onPrimaryDark,
                    ),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = TextStyle(color = onPrimaryDark, fontSize = 16.sp)
                )
                Button(
                    onClick = {
                        if (newTagName.value != "") {
                            tag.tagName = newTagName.value
                            TagDBService(context).updateTag(tag)
                        }
                        dialogOpen.value = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = onPrimaryDark),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(text = context.getString(R.string.simple_rename_button_text), color = primaryDark, style = Typography.titleLarge)
                }
            }
        }
    }

    @Composable
    private fun DeleteButton(modifier: Modifier) {
        val confirmPopupVisible = remember { mutableStateOf(false) }
        val decision = remember { mutableStateOf(false) }
        Button(
            onClick = {
                confirmPopupVisible.value = true
            },
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = errorDark),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = context.getString(R.string.simple_delete_button_text), color = onErrorDark, style = Typography.titleLarge)
        }

        if (confirmPopupVisible.value) {
            ConfirmPopup(context).GeneratePopup(
                popupVisible = confirmPopupVisible,
                decision = decision,
                otherPopupVisible = popupVisible
            )
        }

        if (decision.value) {
            TagDBService(context).deleteTagByID(tag.id)
            decision.value = false
            MainActivity.totalDurationUpdate.value = true
            MainActivity.tagsListUpdate.value = true
        }
    }
}