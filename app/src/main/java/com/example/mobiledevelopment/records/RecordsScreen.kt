package com.example.mobiledevelopment.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobiledevelopment.data.GameResultRepository
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun RecordsScreen(
    repository: GameResultRepository
) {
    var onlyBest by rememberSaveable {
        mutableStateOf(true)
    }

    val resultsFlow = remember(repository, onlyBest) {
        if (onlyBest) {
            repository.observeBestResults()
        } else {
            repository.observeResults()
        }
    }

    val results by resultsFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    val dateFormat = remember {
        DateTimeFormatter
            .ofPattern("dd.MM.yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Рекорды",
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Только лучшие результаты",
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = onlyBest,
                onCheckedChange = { onlyBest = it }
            )
        }

        Text(
            text = if (onlyBest) {
                "Один лучший результат каждого игрока"
            } else {
                "Все партии: сначала новые"
            },
            style = MaterialTheme.typography.bodySmall
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Игрок",
                modifier = Modifier.weight(2f)
            )

            Text(
                text = "Очки",
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Сложность",
                modifier = Modifier.weight(1.5f)
            )
        }

        HorizontalDivider()

        if (results.isEmpty()) {
            Text("Результатов пока нет")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(
                    items = results,
                    key = { it.id }
                ) { result ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .padding(end = 8.dp)
                        ) {
                            Text(result.playerName)

                            Text(
                                text = dateFormat.format(
                                    Instant.ofEpochMilli(
                                        result.finishedAt
                                    )
                                ),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Text(
                            text = result.score.toString(),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = result.difficulty,
                            modifier = Modifier.weight(1.5f)
                        )
                    }

                    HorizontalDivider()
                }
            }
        }
    }
}