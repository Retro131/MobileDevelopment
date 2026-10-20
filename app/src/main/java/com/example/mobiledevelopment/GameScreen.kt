package com.example.mobiledevelopment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobiledevelopment.authors.AuthorsScreen
import com.example.mobiledevelopment.data.GameDatabase
import com.example.mobiledevelopment.data.PlayerRepository
import com.example.mobiledevelopment.game.BugGameScreen
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.player.PlayerScreen
import com.example.mobiledevelopment.rules.RulesScreen
import com.example.mobiledevelopment.session.SessionViewModel
import com.example.mobiledevelopment.settings.GameSettingsScreen
import kotlinx.coroutines.launch

@Composable
fun GameScreen() {
    val session: SessionViewModel = viewModel()

    val currentPlayer by
    session.currentPlayer.collectAsStateWithLifecycle()

    val appContext = LocalContext.current.applicationContext

    val repository = remember(appContext) {
        PlayerRepository(
            GameDatabase.getInstance(appContext).playerDao()
        )
    }

    var settings by rememberSaveable {
        mutableStateOf(GameSettings())
    }

    var showGame by rememberSaveable {
        mutableStateOf(false)
    }

    val pagerState = rememberPagerState(
        pageCount = { GameTab.entries.size }
    )

    val scope = rememberCoroutineScope()
    val stateHolder = rememberSaveableStateHolder()

    LaunchedEffect(currentPlayer?.id) {
        if (currentPlayer == null) {
            showGame = false
        }
    }

    if (showGame && currentPlayer != null) {
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
        GameTabs(
            selectedIndex = pagerState.currentPage,
            onTabSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        HorizontalPager(
            state = pagerState,
            key = { index -> GameTab.entries[index].name },
            modifier = Modifier.weight(1f)
        ) { page ->
            val tab = GameTab.entries[page]

            stateHolder.SaveableStateProvider(tab.name) {
                when (tab) {
                    GameTab.PLAYER -> PlayerScreen(
                        session = session,
                        repository = repository,
                        onStartGame = { showGame = true }
                    )

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