package com.example.scoreboard.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.scoreboard.R
import com.example.scoreboard.Tag
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
        Column(
            modifier = Modifier
                .width(300.dp)
                .heightIn(min = 0.dp, max = 500.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = context.getString(R.string.filter_history_tag_selection_popup_title),
                fontSize = 30.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            val tags = TagDBService(context).getAllTags()
            tagListPick.clear()
            tagListPick.addAll(tags.map { MutablePair(it, false) })
            TagSelectionList()

            Button(
                onClick = {
                    popupVisible.value = false
                    filterTag()
                },
                modifier = Modifier
                    .padding(10.dp)
                    .clickable {
                        filterTag()
                        popupVisible.value = false
                    },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text(text = context.getString(R.string.filter_history_tag_selection_popup_apply_button_text))
            }
        }
    }

    @Composable
    private fun TagSelectionList() {

        LazyColumn(
            modifier = Modifier
                .height(200.dp)
                .widthIn(min = 0.dp, max = 500.dp)
                .padding(vertical = 10.dp, horizontal = 20.dp)
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
            Color.Green
        } else {
            Color.Black
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

    private fun filterTag() {
        val selectedTags = tagListPick.filter { it.right }.map { it.left }
        val selectedTagsIDs = selectedTags.map { it.id }

        sessions.clear()
        sessions.addAll(SessionTagDBService(context).getSessionsForTagIDs(selectedTagsIDs))
        sessions.sortByDescending { it.getDate().timeInMillis }
    }
}