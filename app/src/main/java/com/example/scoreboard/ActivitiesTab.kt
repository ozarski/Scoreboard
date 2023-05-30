package com.example.scoreboard

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.example.scoreboard.database.StatsDBService
import com.example.scoreboard.popups.PopupGenerator
import com.example.scoreboard.popups.WindowCenterOffsetPositionProvider

class ActivitiesTab(private val context: Context) : ComponentActivity() {

    @Composable
    fun GenerateLayout() {
        val popupVisible = remember { mutableStateOf(false) }
        val hourPickerValue = remember { mutableStateOf<Hours>(FullHours(0, 0)) }
        ActivitiesTabLayout(popupVisible, hourPickerValue)
    }

    @Composable
    fun ActivitiesTabLayout(popupVisible: MutableState<Boolean>, hourPickerValue: MutableState<Hours>) {
        if (popupVisible.value) {
            PopupGenerator.getInstance(context).AddSessionPopup(popupVisible, hourPickerValue)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            TotalDurationTextView()
            ActivitiesDurationLazyColumn()
        }

        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                FloatingActionButton(
                    onClick = {
                        popupVisible.value = true
                    },
                    containerColor = MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Add button",
                        tint = Color.White
                    )
                }
            }
        }
    }

    @Composable
    fun TotalDurationTextView() {
        val totalDuration = StatsDBService(context).getTotalDuration()
        val totalDurationString = durationInSecondsToDaysAndHoursAndMinutes(totalDuration)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Total duration text label
            Text(
                text = "Total duration",
                modifier = Modifier.padding(top = 10.dp)
            )
            //Total duration text value
            Text(
                text = totalDurationString,
                fontSize = 25.sp
            )
        }
    }

    @Composable
    fun ActivitiesDurationLazyColumn() {
        var tagsWithDurations = StatsDBService(context).getAllTagsWithDurations()
        tagsWithDurations = tagsWithDurations.sortedBy { it.first.tagName }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(tagsWithDurations.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                ) {
                    ActivityItem(tagsWithDurations[index])
                }
            }
        }
    }

    @Composable
    fun ActivityItem(activityItem: Pair<Tag, Long>) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = activityItem.first.tagName,
                fontSize = 25.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
            val duration = durationInSecondsToDaysAndHoursAndMinutes(activityItem.second)
            Text(
                text = duration,
                fontSize = 25.sp,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }


}