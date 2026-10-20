package com.example.mobiledevelopment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mobiledevelopment.authors.AuthorsScreen
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.registration.RegistrationConfig
import com.example.mobiledevelopment.registration.RegistrationScreen
import com.example.mobiledevelopment.rules.RulesScreen
import com.example.mobiledevelopment.settings.GameSettingsScreen
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledevelopment.game.BugGameScreen
import kotlinx.coroutines.launch

private enum class GameTab(val titleRes: Int) {
    PLAYER(R.string.tab_player),
    RULES(R.string.tab_rules),
    AUTHORS(R.string.tab_authors),
    SETTINGS(R.string.tab_settings)
}

@Composable
fun GameScreen() {
    var selectedTab by rememberSaveable {
        mutableStateOf(GameTab.PLAYER)
    }
    var settings by rememberSaveable {
        mutableStateOf(GameSettings())
    }
    var showGame by rememberSaveable {
        mutableStateOf(false)
    }
    val pagerState = rememberPagerState(pageCount = { GameTab.entries.size })
    val coroutineScope = rememberCoroutineScope()
    val stateHolder = rememberSaveableStateHolder()
    val config = remember { RegistrationConfig() }
    if (showGame) {
        BugGameScreen(
            settings = settings,
            onExit = { showGame = false }
        )
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Button(
            onClick = { showGame = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("Играть")
        }
        PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
            GameTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    modifier = Modifier.height(48.dp)
                ) {
                    if (tab == GameTab.SETTINGS) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = stringResource(tab.titleRes),
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = stringResource(tab.titleRes),
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            key = { index -> GameTab.entries[index].name },
            modifier = Modifier.weight(1f)
        )
        { page ->
            val tab = GameTab.entries[page]
            stateHolder.SaveableStateProvider(tab.name) {
                when (tab) {
                    GameTab.PLAYER -> RegistrationScreen(config, settings)
                    GameTab.RULES -> RulesScreen()
                    GameTab.AUTHORS -> AuthorsScreen()
                    GameTab.SETTINGS -> GameSettingsScreen(
                        settings = settings,
                        onSettingsChange = { settings = it }
                    )
                }
            }
        }
    }
}