package com.example.mobiledevelopment.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.model.Player
import java.time.format.DateTimeFormatter

@Composable
fun PlayerSelectionScreen(
    players: List<Player>,
    onSelectPlayer: (Player) -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Выбери игрока")

        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрировать нового")
        }

        TextButton(onClick = onBack) {
            Text("Назад")
        }

        if (players.isEmpty()) {
            Text("Сохранённых игроков пока нет")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = players,
                    key = { it.id }
                ) { player ->
                    Button(
                        onClick = { onSelectPlayer(player) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(player.fullName)

                            Text(
                                "Дата рождения: ${
                                    player.birthDate?.format(
                                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    ) ?: "Не указана"
                                }"
                            )

                            Text("Курс: ${player.course ?: "Не указан"}")
                            Text("Пол: ${player.gender?.title ?: "Не указан"}")
                            Text("ID: ${player.id}")
                        }
                    }
                }
            }
        }
    }
}