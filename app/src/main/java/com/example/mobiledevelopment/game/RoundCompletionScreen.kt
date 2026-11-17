package com.example.mobiledevelopment.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RoundCompletionScreen(
    state: GameState,
    onRetrySave: () -> Unit,
    onReplay: () -> Unit,
    onExit: () -> Unit
) {
    if (state.resultSaved) {
        RoundResultScreen(
            score = state.score,
            hits = state.hits,
            misses = state.misses,
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
        Text("Очки: ${state.score}")

        if (state.isSaving) {
            CircularProgressIndicator()
            Text("Сохранение результата…")
        }

        val error = state.saveError

        if (error != null) {
            Text(error)

            Button(
                onClick = onRetrySave,
                enabled = !state.isSaving
            ) {
                Text("Повторить сохранение")
            }

            OutlinedButton(
                onClick = onExit,
                enabled = !state.isSaving
            ) {
                Text("Выйти без сохранения")
            }
        }
    }
}