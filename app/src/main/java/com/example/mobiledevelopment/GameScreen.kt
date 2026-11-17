package com.example.mobiledevelopment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobiledevelopment.authors.AuthorsScreen
import com.example.mobiledevelopment.data.GameResultRepository
import com.example.mobiledevelopment.data.PlayerRepository
import com.example.mobiledevelopment.game.BugGameScreen
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.player.PlayerScreen
import com.example.mobiledevelopment.records.RecordsScreen
import com.example.mobiledevelopment.rules.RulesScreen
import com.example.mobiledevelopment.session.SessionViewModel
import com.example.mobiledevelopment.settings.GameSettingsScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun GameScreen() {
    val session: SessionViewModel = koinViewModel()

    val currentPlayer by
    session.currentPlayer.collectAsStateWithLifecycle()

    val repository: PlayerRepository = koinInject()

    val resultRepository: GameResultRepository = koinInject()

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

    val player = currentPlayer

    if (showGame && player != null) {
        BugGameScreen(
            playerId = player.id,
            settings = settings,
            onSaveResult = { playerId, score, roundSettings ->
                resultRepository.saveResult(
                    playerId = playerId,
                    score = score,
                    settings = roundSettings
                )
            },
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

                    GameTab.RECORDS -> RecordsScreen(
                        repository = resultRepository
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