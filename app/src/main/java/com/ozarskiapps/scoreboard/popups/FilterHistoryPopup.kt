package com.ozarskiapps.scoreboard.popups

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.base.Tag
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.inverseOnSurfaceDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.onTertiaryContainerDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import org.apache.commons.lang3.tuple.MutablePair
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FilterHistoryPopup(val context: Context, val activity: Activity = context as Activity) {
    private lateinit var popupVisible: MutableState<Boolean>
    private lateinit var tagPickList: SnapshotStateList<MutablePair<MutableState<Tag>, MutableState<Boolean>>>
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var datePickerState: DateRangePickerState


    @Composable
    fun GeneratePopup(
        popupVisible: MutableState<Boolean>
    ) {
        this.popupVisible = popupVisible
        MainActivity.tagListPick.map {
            MutablePair(
                remember { mutableStateOf(it.left) },
                remember { mutableStateOf(it.right) })
        }.run {
            tagPickList = remember { mutableStateListOf() }
            tagPickList.addAll(this.sortedBy { it.left.value.tagName.lowercase() })
        }
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
        ) {
            Text(
                text = context.getString(R.string.filter_history_tag_selection_popup_title),
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 10.dp),
                style = Typography.titleLarge,
                color = onPrimaryDark
            )
            DateRangeRow()
            TagSelectionList()
            ApplyResetButtons()
        }
    }

    @Composable
    fun ApplyResetButtons() {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            val buttonModifier = Modifier
                .padding(vertical = 10.dp, horizontal = 20.dp)
                .weight(1f)
            ApplyChangesButton(
                text = context.getString(R.string.filter_history_tag_selection_popup_apply_button_text),
                modifier = buttonModifier
            ) {
                popupVisible.value = false
                filterTags()
            }
            ApplyChangesButton(
                text = context.getString(R.string.reset_filters_button_text),
                modifier = buttonModifier
            ) {
                resetTags()
            }
        }
    }

    @Composable
    fun ApplyChangesButton(text: String, modifier: Modifier, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = onPrimaryDark),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = text, color = primaryDark, style = Typography.titleLarge)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    fun DateRangeRow() {
        datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = MainActivity.startDate,
            initialSelectedEndDateMillis = MainActivity.endDate
        )
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        val startDateString =
            datePickerState.selectedStartDateMillis?.let { sdf.format(it) }
        val endDateString =
            datePickerState.selectedEndDateMillis?.let { sdf.format(it) }
        val dateRangeString =
            if (startDateString != null && endDateString != null) {
                "$startDateString - $endDateString"
            } else {
                "Select date range"
            }
        var dateRangePickerVisible by remember {
            mutableStateOf(false)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .clickable { dateRangePickerVisible = true }
        ) {
            Text(
                text = context.getString(R.string.date_range_picker_title),
                fontSize = 20.sp,
                color = onPrimaryDark,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
            )

            Text(
                text = dateRangeString,
                fontSize = 20.sp,
                color = onPrimaryDark,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        if (dateRangePickerVisible) {
            Popup(
                popupPositionProvider = WindowCenterOffsetPositionProvider(), onDismissRequest = {
                    dateRangePickerVisible = false
                }, properties = PopupProperties(focusable = true)
            ) {
                GenericPopupContent.GenerateContent(
                    widthMax = 375,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    backgroundColor = onPrimaryDark,
                    borderColor = primaryDark
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DateRangePicker(
                            datePickerState, colors = DatePickerDefaults.colors(
                                selectedDayContainerColor = inverseOnSurfaceDark,
                            ),
                            title = {
                                Text(
                                    text = "Select date rage",
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(10.dp)
                                )
                            },
                            headline = {
                                Text(
                                    text = dateRangeString,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                            },
                            showModeToggle = false,
                            modifier = Modifier.heightIn(0.dp, 500.dp)
                        )
                        Button(
                            onClick = {
                                dateRangePickerVisible = false
                            },
                            modifier = Modifier.padding(10.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryDark),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(
                                text = "Apply",
                                color = onPrimaryDark,
                                style = Typography.titleLarge
                            )
                        }
                    }
                }
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
                    color = onPrimaryDark,
                    shape = RoundedCornerShape(25.dp)
                )
        ) {
            items(tagPickList.size) { index ->
                TagPickListItem(tagPickList[index])
            }
        }
    }


    @Composable
    private fun TagPickListItem(
        item: MutablePair<MutableState<Tag>, MutableState<Boolean>>
    ) {

        val contentColor = if (item.right.value) {
            onPrimaryDark
        } else {
            onTertiaryContainerDark
        }

        val iconResource = if (item.right.value) {
            painterResource(R.drawable.baseline_label_24)
        } else {
            painterResource(R.drawable.outline_label_24)
        }

        val interactionSource = remember { MutableInteractionSource() }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 20.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) {
                    item.right.value = !item.right.value
                }
        ) {
            Icon(
                painter = iconResource,
                contentDescription = "Tag icon",
                tint = contentColor,
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = item.left.value.tagName,
                fontSize = 20.sp,
                color = contentColor,
                modifier = Modifier
                    .padding(start = 5.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun filterTags() {
        MainActivity.loadMoreSessions(
            context,
            true,
            tagPickList.map { MutablePair(it.left.value, it.right.value) },
            datePickerState.selectedStartDateMillis,
            datePickerState.selectedEndDateMillis
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun resetTags() {
        datePickerState.setSelection(null, null)
        tagPickList.forEach { it.right.value = false }
        MainActivity.loadMoreSessions(
            context,
            true,
            tagPickList.map { MutablePair(it.left.value, it.right.value) }
        )
    }
}