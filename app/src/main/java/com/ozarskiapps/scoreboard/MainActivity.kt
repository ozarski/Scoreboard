package com.ozarskiapps.scoreboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.base.Tag
import com.example.base.session.Session
import com.example.database.DatabaseConstants
import com.example.database.ScoreboardDatabase
import com.example.database.SessionTagDBService
import com.example.database.StatsDBService
import com.example.database.TagDBService
import com.ozarskiapps.scoreboard.popups.ConfirmImportPopup
import com.ozarskiapps.scoreboard.ui.ActivitiesTab
import com.ozarskiapps.scoreboard.ui.HistoryTab
import com.ozarskiapps.scoreboard.ui.theme.ScoreboardTheme
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.backgroundDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import org.apache.commons.lang3.tuple.MutablePair
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private lateinit var confirmImportPopupVisible: MutableState<Boolean>
    private var importUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            tagListPick = TagDBService(this).getAllTags().map { MutablePair(it, false) }
            totalDuration = remember { mutableLongStateOf(StatsDBService(this).getTotalDuration()) }
            tagList = remember { mutableStateListOf() }
            loadMoreTags(this)
            sessionList = remember { mutableStateListOf() }
            filteredDuration = remember { mutableLongStateOf(0) }
            loadMoreSessions(this)
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

            confirmImportPopupVisible = remember { mutableStateOf(false) }
            val decision = remember { mutableStateOf(false) }
            if (confirmImportPopupVisible.value) {
                ConfirmImportPopup(this@MainActivity).GeneratePopup(
                    popupVisible = confirmImportPopupVisible,
                    decision = decision
                )
            }
            if (decision.value) {
                importDatabaseFile()
                decision.value = false
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
    fun MainTabsPager(pagerState: PagerState, modifier: Modifier) {
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
                    HistoryTab(this@MainActivity, this@MainActivity).GenerateLayout()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_DB_REQUEST_CODE) {
            data?.data?.let {
                copyFileToAppDatabasesFolder(it)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun copyFileToAppDatabasesFolder(
        uri: Uri,
        targetFilename: String = DatabaseConstants.DATABASE_NAME
    ) {
        val toDir = File("$dataDir/databases")

        if (!toDir.exists()) {
            toDir.mkdirs()
        }

        val schemaCheckFile = File(toDir, DatabaseConstants.SCHEMA_CHECK_DATABASE_NAME)
        copyFile(uri, schemaCheckFile)
        ScoreboardDatabase(this@MainActivity, DatabaseConstants.SCHEMA_CHECK_DATABASE_NAME).run {
            checkDatabaseSchema()
            if (!checkDatabaseSchema()) {
                Toast.makeText(this@MainActivity, "Wrong data format!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        confirmImportPopupVisible.value = true
        schemaCheckFile.delete()
        importUri = uri
    }

    private fun importDatabaseFile() {
        val toFile = File("$dataDir/databases/${DatabaseConstants.DATABASE_NAME}")
        importUri?.let {
            copyFile(it, toFile)
            loadMoreTags(this, true)
            loadMoreSessions(this, true)
        }
    }

    private fun copyFile(from: Uri, to: File) {
        val inputStream = contentResolver.openInputStream(from)
        val outputStream = FileOutputStream(to)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
    }

    companion object {
        const val PICK_DB_REQUEST_CODE = 100
        lateinit var tagList: SnapshotStateList<Pair<Tag, Long>>
        lateinit var sessionList: SnapshotStateList<Session>
        lateinit var totalDuration: MutableLongState
        lateinit var filteredDuration: MutableLongState
        lateinit var loadMoreSessionsButtonVisible: MutableState<Boolean>
        lateinit var loadMoreTagsButtonVisible: MutableState<Boolean>

        var tagListPick = listOf<MutablePair<Tag, Boolean>>()

        private var tagsPage = 1
        private var sessionsPage = 1

        fun loadMoreTags(context: Context, reload: Boolean = false) {
            if (reload) {
                tagsPage = 1
                tagList.clear()
                tagListPick = TagDBService(context).getAllTags().map { MutablePair(it, false) }
                loadMoreTagsButtonVisible.value = true
            }
            StatsDBService(context).getAllTagsWithDurations(tagsPage).run {
                if (isEmpty()) {
                    Toast.makeText(context, "No more tags to load", Toast.LENGTH_SHORT).show()
                    return
                }
                if (size < DatabaseConstants.DEFAULT_PAGE_SIZE) {
                    loadMoreTagsButtonVisible.value = false
                }
                tagList.addAll(this)
            }
            tagsPage++
            totalDuration.longValue = StatsDBService(context).getTotalDuration()
        }

        fun loadMoreSessions(
            context: Context,
            reload: Boolean = false,
            newTagListPick: List<MutablePair<Tag, Boolean>>? = null
        ) {
            if (reload) {
                sessionsPage = 1
                sessionList.clear()
                loadMoreSessionsButtonVisible.value = true
            }

            val pickedIDs = newTagListPick?.let { tags ->
                tagListPick = tags
                tags.filter { it.right }.map { it.left.id }
            } ?: tagListPick.filter { it.right }.map { it.left.id }

            SessionTagDBService(context).getSessionsForTagIDs(pickedIDs, sessionsPage)
                .sortedByDescending {
                    it.getDate().timeInMillis
                }.run {
                    if (isEmpty()) {
                        Toast.makeText(context, "All sessions loaded", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if (size < DatabaseConstants.DEFAULT_PAGE_SIZE) {
                        loadMoreSessionsButtonVisible.value = false
                    }
                    sessionList.addAll(this)
                }

            sessionsPage++

            filteredDuration.longValue =
                StatsDBService(context).getDurationForSessionsWithTags(pickedIDs)
        }
    }
}