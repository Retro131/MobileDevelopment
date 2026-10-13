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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.R

private const val FIELD_SIZE = 1000f
private const val MISS_PENALTY = 1

private fun createBugs(): List<Bug> {
    return listOf(
        DefaultBug(
            id = 1,
            x = 250f,
            y = 300f
        ),
        DefaultBug(
            id = 2,
            x = 930f,
            y = 759f
        ),
        DefaultBug(
            id = 3,
            x = 500f,
            y = 900f
        ),
    )
}

@Composable
fun BugGameScreen() {

    var bugs by remember { mutableStateOf(createBugs()) }
    var score by remember { mutableIntStateOf(0) }

    val bugPainter = painterResource(R.drawable.bug)

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

                        val fieldWidth = size.width / scale
                        val fieldHeight = size.height / scale

                        val modelX = touch.x / scale
                        val modelY = touch.y / scale

                        if (
                            modelX !in 0f..fieldWidth ||
                            modelY !in 0f..fieldHeight
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

            drawRect(
                color = Color(0xFFE8F5E9),
            )

            bugs.forEach { bug ->
                val imageSize = bug.size * scale

                val left = (bug.x - bug.radius) * scale
                val top = (bug.y - bug.radius) * scale

                translate(left = left, top = top) {
                    with(bugPainter) {
                        draw(
                            size = Size(
                                width = imageSize,
                                height = imageSize
                            )
                        )
                    }
                }
            }

        }
    }
}
