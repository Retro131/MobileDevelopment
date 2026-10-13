package com.example.mobiledevelopment.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

private const val FIELD_SIZE = 1000f
private const val MISS_PENALTY = 5

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
    var score by remember { mutableIntStateOf(0) }

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

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectTapGestures { touch ->
                        val scale = minOf(
                            size.width,
                            size.height
                        ).toFloat() / FIELD_SIZE

                        if (scale <= 0f) {
                            return@detectTapGestures
                        }

                        val left = (size.width - FIELD_SIZE * scale) / 2f
                        val top = (size.height - FIELD_SIZE * scale) / 2f

                        val modelX = (touch.x - left) / scale
                        val modelY = (touch.y - top) / scale

                        if (
                            modelX !in 0f..FIELD_SIZE ||
                            modelY !in 0f..FIELD_SIZE
                        ) {
                            return@detectTapGestures
                        }

                        val hitBug = bugs.lastOrNull { bug ->
                            bug.contains(modelX, modelY)
                        }

                        if (hitBug != null) {
                            bugs = bugs.filterNot { bug ->
                                bug.id == hitBug.id
                            }

                            score += hitBug.points
                        } else {
                            score -= MISS_PENALTY
                        }
                        println("Нажатие ${touch.x} ${touch.y}")
                    }
                }
        ) {
            val scale = minOf(size.width, size.height) / FIELD_SIZE

            val left = (size.width - FIELD_SIZE * scale) / 2f
            val top = (size.height - FIELD_SIZE * scale) / 2f

            drawRect(
                color = Color(0xFFE8F5E9),
                topLeft = Offset(left, top),
                size = Size(
                    width = FIELD_SIZE * scale,
                    height = FIELD_SIZE * scale
                )
            )

            bugs.forEach { bug ->
                val center = Offset(
                    x = left + bug.x * scale,
                    y = top + bug.y * scale
                )

            }

        }
    }


}