package com.example.mobiledevelopment.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.model.GameSettings
import kotlinx.coroutines.CancellationException

@Composable
fun RoundCompletionScreen(
    playerId: Long,
    settings: GameSettings,
    score: Int,
    hits: Int,
    misses: Int,
    onSaveResult: suspend (Long, Int, GameSettings) -> Unit,
    onReplay: () -> Unit,
    onExit: () -> Unit
) {
    var saved by remember {
        mutableStateOf(false)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var saveAttempt by remember {
        mutableIntStateOf(0)
    }

    BackHandler(enabled = !saved) {}

    LaunchedEffect(saveAttempt) {
        if (!saved) {
            try {
                onSaveResult(playerId, score, settings)
                saved = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = "Не удалось сохранить результат"
            }
        }
    }

    if (saved) {
        RoundResultScreen(
            score = score,
            hits = hits,
            misses = misses,
            onReplay = onReplay,
            onExit = onExit
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Раунд завершён")
        Text("Очки: $score")

        val message = error

        if (message == null) {
            CircularProgressIndicator()
            Text("Сохранение результата…")
        } else {
            Text(message)

            Button(
                onClick = {
                    error = null
                    saveAttempt++
                }
            ) {
                Text("Повторить сохранение")
            }

            OutlinedButton(onClick = onExit) {
                Text("Выйти без сохранения")
            }
        }
    }
}