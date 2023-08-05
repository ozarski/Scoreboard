package com.example.scoreboard.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.scoreboard.R
import com.example.scoreboard.Tag
import com.example.scoreboard.database.SessionDBService
import com.example.scoreboard.database.SessionTagDBService
import com.example.scoreboard.database.TagDBService
import com.example.scoreboard.session.Session
import org.apache.commons.lang3.tuple.MutablePair

class FilterHistoryPopup(val context: Context) {
    private lateinit var popupVisible: MutableState<Boolean>
    private lateinit var tagListPick: SnapshotStateList<MutablePair<Tag, Boolean>>
    private lateinit var sessions: SnapshotStateList<Session>

    @Composable
    fun GeneratePopup(
        popupVisible: MutableState<Boolean>,
        sessions: SnapshotStateList<Session>
    ) {
        this.popupVisible = popupVisible
        this.sessions = sessions
        tagListPick = remember { mutableStateListOf() }
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = {
                popupVisible.value = false
            },
            properties = PopupProperties(focusable = true)
        ) {
            FilterHistoryPopupLayout()
        }
    }

    @Composable
    private fun FilterHistoryPopupLayout() {
        GenericPopupContent.GenerateContent(
            width = 375,
            heightMin = 0,
            heightMax = 500,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = context.getString(R.string.filter_history_tag_selection_popup_title),
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            val tags = TagDBService(context).getAllTags()
            tagListPick.clear()
            tagListPick.addAll(tags.map { MutablePair(it, false) })
            TagSelectionList()

            ApplyResetButtons()
        }
    }

    @Composable
    fun ApplyResetButtons() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    popupVisible.value = false
                    filterTags()
                },
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp, bottom = 10.dp, top = 10.dp)
                    .clickable {
                        filterTags()
                        popupVisible.value = false
                    }
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(context.getColor(R.color.main_ui_buttons_color))),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text(text = context.getString(R.string.filter_history_tag_selection_popup_apply_button_text))
            }

            Button(
                onClick = {
                    resetTags()
                    popupVisible.value = false
                },
                modifier = Modifier
                    .padding(start = 10.dp, end = 20.dp, bottom = 10.dp, top = 10.dp)
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(context.getColor(R.color.main_ui_buttons_color))),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text(text = context.getString(R.string.reset_filters_button_text))
            }
        }
    }

    @Composable
    private fun TagSelectionList() {

        LazyColumn(
            modifier = Modifier
                .heightIn(min = 0.dp, max = 300.dp)
                .width(375.dp)
                .padding(horizontal = 20.dp)
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
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp)
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
                    .clickable {
                        tagPicked.value = !tagPicked.value
                        item.right = tagPicked.value
                    }
                    .padding(start = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

    }

    private fun filterTags() {
        val selectedTags = tagListPick.filter { it.right }.map { it.left }
        val selectedTagsIDs = selectedTags.map { it.id }

        sessions.clear()
        sessions.addAll(SessionTagDBService(context).getSessionsForTagIDs(selectedTagsIDs))
        sessions.sortByDescending { it.getDate().timeInMillis }
    }

    private fun resetTags() {
        sessions.clear()
        sessions.addAll(SessionDBService(context).getAllSessions())
        sessions.sortByDescending { it.getDate().timeInMillis }
    }
}