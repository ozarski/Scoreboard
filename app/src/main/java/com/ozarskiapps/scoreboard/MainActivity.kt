package com.ozarskiapps.scoreboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ozarskiapps.scoreboard.ui.ActivitiesTab
import com.ozarskiapps.scoreboard.ui.HistoryTab
import com.ozarskiapps.scoreboard.ui.theme.ScoreboardTheme
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.backgroundDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
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
            com.ozarskiapps.scoreboard.MainActivity.Companion.totalDurationUpdate = remember { mutableStateOf(true) }
            com.ozarskiapps.scoreboard.MainActivity.Companion.sessionsListUpdate = remember { mutableStateOf(true) }
            com.ozarskiapps.scoreboard.MainActivity.Companion.tagsListUpdate = remember { mutableStateOf(true) }
            ScoreboardTheme {
                LayoutMain()
            }
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
                .background(color = backgroundDark),
            verticalArrangement = Arrangement.Bottom
        ) {
            val pagesModifier = Modifier.weight(1f)
            MainTabsPager(pagerState = pagerState, tabs = tabs, modifier = pagesModifier)
            Row(modifier = Modifier.padding(horizontal = 5.dp)) {
                TabRow(
                    selectedTabIndex = tabIndex.value,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier
                                .pagerTabIndicatorOffset(pagerState, tabPositions)
                                .height(3.dp)
                                .padding(horizontal = 60.dp)
                                .offset(y = ((-6).dp))
                                .clip(shape = RoundedCornerShape(16.dp)),
                            color = primaryDark,
                        )
                    },
                    backgroundColor = onPrimaryDark,
                    modifier = Modifier.clip(
                        shape = RoundedCornerShape(
                            topStart = 25.dp,
                            topEnd = 25.dp
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
            Tab(text = { Text(title, style = Typography.titleLarge, color = primaryDark) },
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
        lateinit var totalDurationUpdate: MutableState<Boolean>
        lateinit var tagsListUpdate: MutableState<Boolean>
        lateinit var sessionsListUpdate: MutableState<Boolean>
    }
}