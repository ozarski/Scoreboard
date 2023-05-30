package com.example.scoreboard

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.scoreboard.database.StatsDBService
import com.example.scoreboard.popups.AddSessionPopup
import com.example.scoreboard.popups.TagDetailsPopup

class ActivitiesTab(private val context: Context) : ComponentActivity() {

    @Composable
    fun GenerateLayout() {
        val popupVisible = remember { mutableStateOf(false) }
        val totalDuration = remember { mutableStateOf(StatsDBService(context).getTotalDuration()) }
        val tagsWithDurations =
            remember { mutableStateOf(StatsDBService(context).getAllTagsWithDurations()) }
        ActivitiesTabLayout(popupVisible, totalDuration, tagsWithDurations)
    }

    @Composable
    fun ActivitiesTabLayout(
        popupVisible: MutableState<Boolean>,
        totalDuration: MutableState<Long>,
        tagsWithDurations: MutableState<List<Pair<Tag, Long>>>
    ) {
        if (popupVisible.value) {
            AddSessionPopup(context).GeneratePopup(popupVisible)
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            TotalDurationTextView(totalDuration)
            ActivitiesDurationLazyColumn(tagsWithDurations)
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
    fun TotalDurationTextView(totalDuration: MutableState<Long>) {
        val totalDurationString = durationInSecondsToDaysAndHoursAndMinutes(totalDuration.value)

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
    fun ActivitiesDurationLazyColumn(tagsWithDurations: MutableState<List<Pair<Tag, Long>>>) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(tagsWithDurations.value.size) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                ) {
                    ActivityItem(tagsWithDurations.value[index])
                }
            }
        }
    }

    @Composable
    fun ActivityItem(
        activityItem: Pair<Tag, Long>
    ) {
        val sessionPopupVisible = remember { mutableStateOf(false) }
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                sessionPopupVisible.value = true
            }, horizontalArrangement = Arrangement.SpaceBetween) {
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

        if (sessionPopupVisible.value) {
            TagDetailsPopup(context, activityItem.first).GeneratePopup(sessionPopupVisible)
        }
    }


}