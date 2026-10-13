package com.example.mobiledevelopment.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private fun createBugs(): List<Bug> {
    return listOf(
        Bug(id = 1, x = 250f, y = 300f, size = 140f, points = 10),
        Bug(id = 2, x = 700f, y = 450f, size = 140f, points = 10),
        Bug(id = 3, x = 450f, y = 750f, size = 140f, points = 10)
    )
}

@Composable
fun BugGameScreen() {

    var bugs by remember { mutableStateOf(createBugs()) }
    var score by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Очки: $score",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(text = "Жуков осталось: ${bugs.size}")

        Button(
            onClick = {
                bugs = createBugs()
                score = 0
            }
        ) {
            Text(text = "Начать заново")
        }

    }


}