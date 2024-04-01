package com.ozarskiapps.scoreboard.ui

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.base.Tag
import com.ozarskiapps.global.durationInSecondsToDaysAndHoursAndMinutes
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.popups.AddSessionPopup
import com.ozarskiapps.scoreboard.popups.TagDetailsPopup
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.backgroundDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.onTertiaryContainerDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark

class ActivitiesTab(private val context: Context) : ComponentActivity() {

    private var page = 1

    @Composable
    fun GenerateLayout() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundDark),
            verticalArrangement = Arrangement.Top
        ) {
            TotalDurationTextView()
            ActivitiesHeader()
            ActivitiesDurationLazyColumn()
        }
    }


    @Composable
    fun ActivitiesHeader() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Text(
                    text = context.getString(R.string.activities_tab_header),
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    style = Typography.bodyLarge,
                    color = onTertiaryContainerDark
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_directions_run_24),
                    contentDescription = "Activities icon",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 3.dp),
                    tint = onTertiaryContainerDark
                )
            }
            AddSessionButton()
        }
    }

    @Composable
    fun AddSessionButton() {
        val popupVisible = remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Add button",
                tint = onTertiaryContainerDark,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .size(40.dp)
                    .clickable {
                        popupVisible.value = true
                    }
            )
        }
        if (popupVisible.value) {
            AddSessionPopup(context).GeneratePopup(popupVisible)
        }
    }

    @Composable
    fun TotalDurationTextView() {
        val totalDurationString =
            durationInSecondsToDaysAndHoursAndMinutes(MainActivity.totalDuration.longValue)
        Card(
            modifier = Modifier.padding(10.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = primaryDark, shape = RoundedCornerShape(25.dp)
                    )
            ) {
                //Total duration icon
                Icon(
                    painter = painterResource(id = R.drawable.baseline_access_time_24),
                    contentDescription = "Total duration icon",
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .size(35.dp),
                    tint = onPrimaryDark
                )
                //Total duration text value
                Text(
                    text = totalDurationString,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(end = 15.dp),
                    style = Typography.labelLarge
                )
            }
        }
    }

    @Composable
    fun ActivitiesDurationLazyColumn() {
        MainActivity.loadMoreTagsButtonVisible = remember { mutableStateOf(true) }

        Card(
            modifier = Modifier.padding(10.dp),
            elevation = CardDefaults.cardElevation(3.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = primaryDark, shape = RoundedCornerShape(25.dp)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(MainActivity.tagList.size) { index ->
                    ActivityItem(MainActivity.tagList[index])
                    HorizontalDivider(
                        color = onPrimaryDark,
                        thickness = 0.7.dp,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }
                if(MainActivity.loadMoreTagsButtonVisible.value){
                    item {
                        LoadMoreTagsButton {
                            MainActivity.loadMoreTags(context)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun LoadMoreTagsButton(loadRunnable: () -> Unit) {
        Button(
            onClick = {
                loadRunnable()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = onPrimaryDark
            )
        ) {
            Text(
                text = "Load more",
                fontSize = 20.sp,
                style = Typography.titleLarge,
                color = primaryDark
            )
        }
    }

    @Composable
    fun ActivityItem(
        activityItem: Pair<Tag, Long>
    ) {
        val tagPopupVisible = remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    tagPopupVisible.value = true
                }
                .padding(top = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = activityItem.first.tagName,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .widthIn(min = 0.dp, max = 250.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = onPrimaryDark,
                style = Typography.bodyLarge
            )


            val duration =
                durationInSecondsToDaysAndHoursAndMinutes(activityItem.second)
            Text(
                text = duration,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(end = 10.dp, start = 3.dp)
                    .widthIn(min = 0.dp, max = 300.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                color = onPrimaryDark,
                style = Typography.bodyLarge
            )
        }

        if (tagPopupVisible.value) {
            TagDetailsPopup(context, activityItem.first).GeneratePopup(tagPopupVisible)
        }
    }
}