package com.ozarskiapps.scoreboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.ozarskiapps.scoreboard.ui.ActivitiesTab
import com.ozarskiapps.scoreboard.ui.HistoryTab
import com.ozarskiapps.scoreboard.ui.theme.ScoreboardTheme
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.backgroundDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            totalDurationUpdate = remember { mutableStateOf(true) }
            sessionsListUpdate = remember { mutableStateOf(true) }
            tagsListUpdate = remember { mutableStateOf(true) }
            ScoreboardTheme {
                LayoutMain()
            }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun LayoutMain() {

        val tabs = listOf(
            this.getString(R.string.main_tabs_activity_tab_title),
            this.getString(R.string.main_tabs_history_tab_title)
        )
        val selectedTabIndex = remember {
            mutableIntStateOf(0)
        }
        val pagerState = rememberPagerState {
            tabs.size
        }

        LaunchedEffect(selectedTabIndex.intValue) {
            pagerState.animateScrollToPage(selectedTabIndex.intValue)
        }

        LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
            if (!pagerState.isScrollInProgress) {
                selectedTabIndex.intValue = pagerState.currentPage
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = backgroundDark),
            verticalArrangement = Arrangement.Bottom
        ) {
            MainTabsPager(
                pagerState = pagerState,
                tabs = tabs,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex.intValue,
                indicator = tabIndicator(selectedTabIndex),
                containerColor = onPrimaryDark,
                modifier = Modifier.clip(
                    shape = RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 25.dp
                    )
                )
            ) {
                MainTabs(selectedTabIndex, tabs)
            }
        }
    }

    @Composable
    private fun tabIndicator(selectedTabIndex: MutableState<Int>): @Composable @UiComposable (List<TabPosition>) -> Unit {
        val indicator: @Composable @UiComposable
            (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
            SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTabIndex.value])
                    .height(3.dp)
                    .padding(horizontal = 60.dp)
                    .offset(y = ((-6).dp))
                    .clip(shape = RoundedCornerShape(16.dp)),
                color = primaryDark
            )
        }
        return indicator
    }


    @Composable
    fun MainTabs(
        selectedTabIndex: MutableState<Int>,
        tabs: List<String>
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = { Text(title, style = Typography.titleLarge, color = primaryDark) },
                selected = selectedTabIndex.value == index,
                onClick = {
                    selectedTabIndex.value = index
                }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainTabsPager(pagerState: PagerState, tabs: List<String>, modifier: Modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier,
            beyondBoundsPageCount = 1
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