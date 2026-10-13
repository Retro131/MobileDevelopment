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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.game.Movements.CircularMovement
import com.example.mobiledevelopment.game.Movements.RandomMovement
import com.example.mobiledevelopment.game.Movements.ZigzagMovement
import kotlinx.coroutines.isActive

private const val FIELD_SIZE = 1000f
private const val MISS_PENALTY = 1

private fun createBugs(): List<Bug> {
    return listOf(
        DefaultBug(
            id = 1,
            x = 250f,
            y = 300f,
            velocityX = 120f,
            velocityY = 90f,
            movement = RandomMovement
        ),
        DefaultBug(
            id = 2,
            x = 930f,
            y = 759f,
            velocityX = -120f,
            velocityY = 90f,
            movement = ZigzagMovement
        ),
        DefaultBug(
            id = 3,
            x = 500f,
            y = 900f,
            velocityX = 120f,
            velocityY = -90f,
            movement = CircularMovement
        ),
        DefaultBug(
            id = 4,
            x = 1230f,
            y = 800f,
            velocityX = 120f,
            velocityY = -90f,
            movement = RandomMovement
        ),
        DefaultBug(
            id = 5,
            x = 300f,
            y = 700f,
            velocityX = 120f,
            velocityY = -90f,
            movement = CircularMovement
        )
    )
}

@Composable
fun BugGameScreen() {

    var bugs by remember { mutableStateOf(createBugs()) }
    var score by remember { mutableIntStateOf(0) }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    val bugPainters = bugs
        .map { it.imageRes }
        .distinct()
        .associateWith { imageRes ->
            painterResource(imageRes)
        }

    LaunchedEffect(canvasSize) {
        if (canvasSize.width == 0 || canvasSize.height == 0) {
            return@LaunchedEffect
        }

        val scale = minOf(
            canvasSize.width,
            canvasSize.height
        ).toFloat() / FIELD_SIZE

        val fieldWidth = canvasSize.width / scale
        val fieldHeight = canvasSize.height / scale

        var previousFrame = withFrameNanos { it }

        while (isActive) {
            val currentFrame = withFrameNanos { it }

            val deltaSeconds = (
                    (currentFrame - previousFrame) / 1_000_000_000f
                    ).coerceIn(0f, 0.05f)

            previousFrame = currentFrame

            bugs = bugs.map { bug ->
                bug.move(
                    deltaSeconds = deltaSeconds,
                    fieldWidth = fieldWidth,
                    fieldHeight = fieldHeight
                )
            }
        }
    }

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
                .onSizeChanged { newSize ->
                    canvasSize = newSize
                }
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
                val painter = bugPainters.getValue(bug.imageRes)

                val imageSize = bug.size * scale

                val left = (bug.x - bug.radius) * scale
                val top = (bug.y - bug.radius) * scale

                translate(left = left, top = top) {
                    with(painter) {
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
