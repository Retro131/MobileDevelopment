package com.example.mobiledevelopment.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun RoundResultScreen(
    score: Int,
    hits: Int,
    misses: Int,
    onReplay: () -> Unit,
    onExit: () -> Unit
) {
    val total = hits + misses
    val accuracy = if (total == 0) {
        0f
    } else {
        hits * 100f / total
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Раунд завершён",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Очки: $score",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(text = "Попадания: $hits")
        Text(text = "Промахи: $misses")

        Text(
            text = "Точность: " +
                    String.format(Locale.getDefault(), "%.1f%%", accuracy)
        )

        Button(
            onClick = onReplay,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Играть ещё")
        }

        OutlinedButton(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("В меню")
        }
    }
}