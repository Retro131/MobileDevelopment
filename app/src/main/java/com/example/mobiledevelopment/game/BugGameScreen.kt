package com.example.mobiledevelopment.game

import GameLayout
import android.media.MediaPlayer
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.game.bonuses.AttractionBonus
import com.example.mobiledevelopment.game.bonuses.Bonus
import com.example.mobiledevelopment.game.bonuses.DioBonus
import com.example.mobiledevelopment.game.bonuses.GravityBonus
import com.example.mobiledevelopment.game.sensors.rememberGravityTilt
import com.example.mobiledevelopment.gold.GoldPointsCalculator
import com.example.mobiledevelopment.gold.GoldQuoteStorage
import com.example.mobiledevelopment.gold.GoldRepository
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.model.GameSettingsLimits
import kotlinx.coroutines.isActive
import kotlin.random.Random
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

private const val FIELD_SIZE = 1000f
private const val MISS_PENALTY = 1
private const val GOLD_INTERVAL_MILLIS = 20_000L

@Composable
fun BugGameScreen(
    playerId: Long,
    settings: GameSettings,
    onSaveResult: suspend (Long, Int, GameSettings) -> Unit,
    onExit: () -> Unit
) {
    var roundNumber by remember {
        mutableIntStateOf(0)
    }

    BackHandler(onBack = onExit)

    key(roundNumber) {
        val roundPlayerId = remember { playerId }

        val roundSettings = remember {
            settings.copy(
                speed = settings.speed.coerceIn(
                    GameSettingsLimits.speed
                ),
                maxCockroaches = settings.maxCockroaches.coerceIn(
                    GameSettingsLimits.maxCockroaches
                ),
                bonusInterval = settings.bonusInterval.coerceIn(
                    GameSettingsLimits.bonusInterval
                ),
                roundDuration = settings.roundDuration.coerceIn(
                    GameSettingsLimits.roundDuration
                )
            )
        }

        BugRound(
            playerId = roundPlayerId,
            settings = roundSettings,
            onSaveResult = onSaveResult,
            onReplay = { roundNumber++ },
            onExit = onExit
        )
    }
}

@Composable
private fun BugRound(
    playerId: Long,
    settings: GameSettings,
    onSaveResult: suspend (Long, Int, GameSettings) -> Unit,
    onReplay: () -> Unit,
    onExit: () -> Unit
) {
    var bugs by remember { mutableStateOf<List<Bug>>(emptyList()) }

    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }

    var remainingSeconds by remember {
        mutableIntStateOf(settings.roundDuration)
    }

    var finished by remember { mutableStateOf(false) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    var startedAt by remember { mutableLongStateOf(-1L) }
    var nextId by remember { mutableIntStateOf(1) }

    val appContext = LocalContext.current.applicationContext

    val goldRepository = remember(appContext) {
        GoldRepository(
            storage = GoldQuoteStorage(appContext)
        )
    }

    var goldLoading by remember {
        mutableStateOf(true)
    }

    var goldPoints by remember {
        mutableStateOf<Int?>(null)
    }

    var goldError by remember {
        mutableStateOf<String?>(null)
    }

    var nextGoldAt by remember {
        mutableLongStateOf(GOLD_INTERVAL_MILLIS)
    }

    val durationMillis = settings.roundDuration * 1000L


    var fieldBonus by remember {
        mutableStateOf<Bonus?>(null)
    }

    var activeBonus by remember {
        mutableStateOf<Bonus?>(null)
    }

    var bonusRemaining by remember {
        mutableFloatStateOf(0f)
    }

    var timeUntilBonus by remember {
        mutableFloatStateOf(settings.bonusInterval.toFloat())
    }

    val tilt by rememberGravityTilt(
        enabled = !finished && activeBonus is GravityBonus
    )

    val bugPainters = bugs
        .map { it.imageRes }
        .distinct()
        .associateWith { imageRes ->
            painterResource(imageRes)
        }

    val bonusToDraw = fieldBonus

    val bonusPainter = bonusToDraw?.let { bonus ->
        painterResource(bonus.imageRes)
    }

    fun createBug(
        fieldWidth: Float,
        fieldHeight: Float
    ): Bug {
        val bug = BugFactory.create(
            id = nextId,
            fieldWidth = fieldWidth,
            fieldHeight = fieldHeight,
            speedMultiplier = settings.speed
        )

        nextId++
        return bug
    }

    fun updateTimer(): Boolean {
        if (finished || startedAt < 0L) {
            return false
        }

        val elapsed = SystemClock.elapsedRealtime() - startedAt
        val remaining = (durationMillis - elapsed).coerceAtLeast(0L)

        remainingSeconds = ((remaining + 999L) / 1000L).toInt()

        if (remaining == 0L) {
            finished = true
            return false
        }

        return true
    }

    LaunchedEffect(Unit) {
        try {
            val quote = goldRepository.getCached()
                ?: withTimeout(10_000L.milliseconds) {
                    goldRepository.loadLatest()
                }

            goldPoints = GoldPointsCalculator.calculate(quote)
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            goldError = "Не удалось получить цену золота за 10 секунд"
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            goldError = "Цена золота недоступна"
        } finally {
            goldLoading = false
        }
    }

    LaunchedEffect(canvasSize, goldLoading) {
        if (
            canvasSize.width == 0 ||
            canvasSize.height == 0 ||
            finished ||
            goldLoading
        ) {
            return@LaunchedEffect
        }

        val scale = minOf(
            canvasSize.width,
            canvasSize.height
        ).toFloat() / FIELD_SIZE

        val fieldWidth = canvasSize.width / scale
        val fieldHeight = canvasSize.height / scale

        if (startedAt < 0L) {
            bugs = List(settings.maxCockroaches) {
                createBug(fieldWidth, fieldHeight)
            }

            startedAt = SystemClock.elapsedRealtime()
        }

        var previousFrame = withFrameNanos { it }

        while (isActive && !finished) {
            val currentFrame = withFrameNanos { it }

            if (!updateTimer()) {
                break
            }

            val elapsed = SystemClock.elapsedRealtime() - startedAt

            if (elapsed >= nextGoldAt) {
                val points = goldPoints

                if (points != null) {
                    val goldBug = BugFactory.createGold(
                        id = nextId,
                        fieldWidth = fieldWidth,
                        fieldHeight = fieldHeight,
                        speedMultiplier = settings.speed,
                        points = points
                    )

                    nextId++

                    bugs = bugs + goldBug
                }

                nextGoldAt =
                    (elapsed / GOLD_INTERVAL_MILLIS + 1L) *
                            GOLD_INTERVAL_MILLIS
            }

            val deltaSeconds = (
                    (currentFrame - previousFrame) / 1_000_000_000f
                    ).coerceIn(0f, 0.05f)

            previousFrame = currentFrame

            timeUntilBonus -= deltaSeconds

            if (timeUntilBonus <= 0f) {
                fieldBonus = BugFactory.createBonus(fieldWidth, fieldHeight)

                timeUntilBonus += settings.bonusInterval
            }

            bugs = bugs.map { bug ->
                bug.move(
                    deltaSeconds = deltaSeconds,
                    fieldWidth = fieldWidth,
                    fieldHeight = fieldHeight,
                    bonusMovement = activeBonus?.movement,
                    tilt = tilt
                )
            }

            if (activeBonus != null) {
                bonusRemaining =
                    (bonusRemaining - deltaSeconds).coerceAtLeast(0f)

                if (bonusRemaining == 0f) {
                    activeBonus = null
                }
            }
        }
    }

    DisposableEffect(activeBonus, finished, appContext) {
        val player = if (!finished) {
            activeBonus?.soundRes?.let { soundRes ->
                MediaPlayer.create(appContext, soundRes)
            }
        } else {
            null
        }

        player?.start()

        onDispose {
            player?.release()
        }
    }

    if (finished) {
        RoundCompletionScreen(
            playerId = playerId,
            settings = settings,
            score = score,
            hits = hits,
            misses = misses,
            onSaveResult = onSaveResult,
            onReplay = onReplay,
            onExit = onExit
        )
        return
    }

    GameLayout(
        controls = {
            Text(
                text = "Очки: $score",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Осталось: $remainingSeconds с",
                style = MaterialTheme.typography.titleLarge
            )

            Text(text = "Попадания: $hits   Промахи: $misses")
            Text(text = "Жуков на поле: ${bugs.size}")

            when {
                goldLoading -> {
                    Text("Подготавливаем цену золота...")
                }

                goldError != null -> {
                    Text(
                        text = goldError ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    Text("Золотой таракан: $goldPoints очков")
                }
            }

            OutlinedButton(onClick = onExit) {
                Text("В меню")
            }
        },
        field = { fieldModifier ->
            Canvas(
                modifier = fieldModifier
                    .onSizeChanged { canvasSize = it }
                    .pointerInput(Unit) {
                        detectTapGestures { touch ->
                            if (!updateTimer()) {
                                return@detectTapGestures
                            }

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

                            val selectedBonus = fieldBonus

                            if (selectedBonus != null &&
                                selectedBonus.contains(modelX, modelY)
                            ) {
                                activeBonus = selectedBonus
                                bonusRemaining = selectedBonus.durationSeconds
                                fieldBonus = null

                                return@detectTapGestures
                            }

                            val hitBug = bugs.lastOrNull { bug ->
                                bug.contains(modelX, modelY)
                            }

                            if (hitBug != null) {
                                hits++
                                score += hitBug.points

                                val remainingBugs = bugs.filterNot {
                                    it.id == hitBug.id
                                }

                                bugs = if (hitBug is GoldBug) {
                                    remainingBugs
                                } else {
                                    val replacement = createBug(
                                        fieldWidth = fieldWidth,
                                        fieldHeight = fieldHeight
                                    )

                                    remainingBugs + replacement
                                }
                            } else {
                                misses++
                                score -= MISS_PENALTY
                            }
                        }
                    }
            ) {
                val scale = minOf(size.width, size.height) / FIELD_SIZE

                drawRect(color = Color(0xFFE8F5E9))

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
                if (bonusToDraw != null && bonusPainter != null) {
                    val imageSize = bonusToDraw.radius * 2f * scale

                    val left = (bonusToDraw.x - bonusToDraw.radius) * scale
                    val top = (bonusToDraw.y - bonusToDraw.radius) * scale

                    translate(left = left, top = top) {
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
    )
}