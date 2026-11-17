package com.example.mobiledevelopment.game

import GameLayout
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.mobiledevelopment.game.bonuses.GravityBonus
import com.example.mobiledevelopment.game.sensors.rememberGravityTilt
import com.example.mobiledevelopment.model.GameSettings
import kotlinx.coroutines.isActive
import org.koin.androidx.compose.koinViewModel

private const val FIELD_SIZE = 1000f

@Composable
fun BugGameScreen(
    playerId: Long,
    settings: GameSettings,
    onExit: () -> Unit,
    viewModel: GameViewModel = koinViewModel()
) {
    val exitGame: () -> Unit = {
        viewModel.exitGame()
        onExit()
    }

    BackHandler {
        val current = viewModel.state

        if (
            current == null ||
            !current.finished ||
            current.resultSaved
        ) {
            exitGame()
        }
    }

    LaunchedEffect(viewModel, playerId) {
        viewModel.ensureGame(
            playerId = playerId,
            settings = settings
        )
    }

    val state = viewModel.state

    if (state == null || state.playerId != playerId) {
        Text("Подготовка игры...")
        return
    }

    BugRound(
        state = state,
        viewModel = viewModel,
        onExit = exitGame
    )
}

@Composable
private fun BugRound(
    state: GameState,
    viewModel: GameViewModel,
    onExit: () -> Unit
) {
    val appContext = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current

    var canvasSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val tilt by rememberGravityTilt(
        enabled = !state.finished &&
                state.activeBonus is GravityBonus
    )

    val latestTilt by rememberUpdatedState(tilt)

    LaunchedEffect(viewModel, canvasSize) {
        if (canvasSize.width <= 0 || canvasSize.height <= 0) {
            return@LaunchedEffect
        }

        val scale = minOf(
            canvasSize.width,
            canvasSize.height
        ).toFloat() / FIELD_SIZE

        viewModel.initializeField(
            width = canvasSize.width / scale,
            height = canvasSize.height / scale
        )
    }

    LaunchedEffect(
        viewModel,
        lifecycleOwner,
        canvasSize,
        state.goldLoading,
        state.finished
    ) {
        if (
            canvasSize.width <= 0 ||
            canvasSize.height <= 0 ||
            state.goldLoading ||
            state.finished
        ) {
            return@LaunchedEffect
        }

        lifecycleOwner.lifecycle.repeatOnLifecycle(
            Lifecycle.State.RESUMED
        ) {
            var previousFrame = withFrameNanos { it }

            while (
                isActive &&
                viewModel.state?.finished == false
            ) {
                val currentFrame = withFrameNanos { it }

                val deltaSeconds = (
                        (currentFrame - previousFrame) /
                                1_000_000_000f
                        ).coerceIn(0f, 0.05f)

                previousFrame = currentFrame

                viewModel.updateFrame(
                    deltaSeconds = deltaSeconds,
                    tilt = latestTilt
                )
            }
        }
    }

    LifecycleResumeEffect(
        state.activeBonus,
        state.finished,
        appContext
    ) {
        val mediaPlayer = if (!state.finished) {
            state.activeBonus?.soundRes?.let { soundRes ->
                MediaPlayer.create(appContext, soundRes)
            }
        } else {
            null
        }

        mediaPlayer?.start()

        onPauseOrDispose {
            mediaPlayer?.release()
        }
    }

    if (state.finished) {
        RoundCompletionScreen(
            state = state,
            onRetrySave = viewModel::retrySaveResult,
            onReplay = viewModel::restartGame,
            onExit = onExit
        )
        return
    }

    GameLayout(
        controls = {
            Text(
                text = "Очки: ${state.score}",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Осталось: ${state.remainingSeconds} с",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Попадания: ${state.hits}   " +
                        "Промахи: ${state.misses}"
            )

            Text("Жуков на поле: ${state.bugs.size}")

            when {
                state.goldLoading -> {
                    Text("Подготавливаем цену золота...")
                }

                state.goldError != null -> {
                    Text(
                        text = state.goldError,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    Text(
                        "Золотой таракан: " +
                                "${state.goldPoints} очков"
                    )
                }
            }

            if (state.activeBonus != null) {
                Text(
                    "Бонус: ${
                        state.bonusRemaining.toInt()
                    } с"
                )
            }

            OutlinedButton(onClick = onExit) {
                Text("В меню")
            }
        },
        field = { fieldModifier ->
            GameField(
                state = state,
                fieldWidth = viewModel.fieldWidth,
                fieldHeight = viewModel.fieldHeight,
                onSizeChanged = { canvasSize = it },
                onTap = viewModel::onTap,
                modifier = fieldModifier
            )
        }
    )
}

@Composable
private fun GameField(
    state: GameState,
    fieldWidth: Float,
    fieldHeight: Float,
    onSizeChanged: (IntSize) -> Unit,
    onTap: (Float, Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val bugPainters = state.bugs
        .map { it.imageRes }
        .distinct()
        .associateWith { imageRes ->
            painterResource(imageRes)
        }

    val bonus = state.fieldBonus

    val bonusPainter = bonus?.let {
        painterResource(it.imageRes)
    }

    val latestOnTap by rememberUpdatedState(onTap)

    Canvas(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged(onSizeChanged)
            .pointerInput(fieldWidth, fieldHeight) {
                detectTapGestures { touch ->
                    if (
                        size.width <= 0 ||
                        size.height <= 0 ||
                        fieldWidth <= 0f ||
                        fieldHeight <= 0f
                    ) {
                        return@detectTapGestures
                    }

                    val scaleX = size.width / fieldWidth
                    val scaleY = size.height / fieldHeight

                    val imageScale =
                        minOf(size.width, size.height) / FIELD_SIZE

                    latestOnTap(
                        touch.x / scaleX,
                        touch.y / scaleY,
                        scaleX / imageScale,
                        scaleY / imageScale
                    )
                }
            }
    ) {
        drawRect(color = Color(0xFFE8F5E9))

        if (fieldWidth > 0f && fieldHeight > 0f) {
            val scaleX = size.width / fieldWidth
            val scaleY = size.height / fieldHeight

            val imageScale = minOf(
                size.width,
                size.height
            ).toFloat() / FIELD_SIZE

            state.bugs.forEach { bug ->
                val painter = bugPainters.getValue(
                    bug.imageRes
                )

                val imageSize = bug.size * imageScale

                val centerX = bug.x * scaleX
                val centerY = bug.y * scaleY

                translate(
                    left = centerX - imageSize / 2f,
                    top = centerY - imageSize / 2f
                ) {
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

            if (bonus != null && bonusPainter != null) {
                val imageSize =
                    bonus.radius * 2f * imageScale

                val centerX = bonus.x * scaleX
                val centerY = bonus.y * scaleY

                translate(
                    left = centerX - imageSize / 2f,
                    top = centerY - imageSize / 2f
                ) {
                    with(bonusPainter) {
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