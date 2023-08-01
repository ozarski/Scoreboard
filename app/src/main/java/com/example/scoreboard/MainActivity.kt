package com.example.scoreboard

import android.app.Fragment
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scoreboard.database.ScoreboardDatabase
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            activitiesDataUpdate = remember { mutableStateOf(false) }
            historyDataUpdate = remember { mutableStateOf(false) }
            LayoutMain()
        }
    }


    @ExperimentalPagerApi
    @Composable
    fun LayoutMain() {

        val tabs = listOf(
            this.getString(R.string.main_tabs_activity_tab_title),
            this.getString(R.string.main_tabs_history_tab_title)
        )
        val tabIndex = remember { mutableStateOf(0) }
        val pagerState = rememberPagerState()
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(this@MainActivity.getColor(R.color.tabs_background_color))),
            verticalArrangement = Arrangement.Bottom
        ) {
            val pagesModifier = Modifier.weight(1f)
            MainTabsPager(pagerState = pagerState, tabs = tabs, modifier = pagesModifier)
            Row(modifier = Modifier.padding(horizontal = 10.dp)) {
                TabRow(
                    selectedTabIndex = tabIndex.value,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .pagerTabIndicatorOffset(pagerState, tabPositions)
                                .offset(y = ((-46).dp)),
                            color = Color(this@MainActivity.getColor((R.color.tab_indicator_color))),

                            )
                    },
                    backgroundColor = Color(this@MainActivity.getColor(R.color.main_ui_buttons_color)),
                    modifier = Modifier.clip(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp
                        )
                    )
                ) {
                    MainTabs(tabIndex, scope, pagerState, tabs)
                }
            }
        }
    }

    @ExperimentalPagerApi
    @Composable
    fun MainTabs(
        tabIndex: MutableState<Int>,
        scope: CoroutineScope,
        pagerState: PagerState,
        tabs: List<String>
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(text = { Text(title, color = Color.White) },
                selected = tabIndex.value == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    tabIndex.value = index
                }
            )
        }
    }

    @ExperimentalPagerApi
    @Composable
    fun MainTabsPager(pagerState: PagerState, tabs: List<String>, modifier: Modifier) {
        HorizontalPager(
            state = pagerState,
            count = tabs.size,
            modifier = modifier
        ) {
            when (it) {
                //0 -> MainBooksUI(context).GenerateLayout()
                //1 -> MainStatsUI(context).GenerateLayout()
                0 -> {
                    ActivitiesTab(this@MainActivity).GenerateLayout()
                }

                1 -> {
                    HistoryTab(this@MainActivity).GenerateLayout()
                }
            }
        }
    }

    companion object {
        lateinit var activitiesDataUpdate: MutableState<Boolean>
        lateinit var historyDataUpdate: MutableState<Boolean>
    }
}