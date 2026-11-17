package com.example.mobiledevelopment.game

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobiledevelopment.data.GameResultRepository
import com.example.mobiledevelopment.gold.GoldPointsCalculator
import com.example.mobiledevelopment.gold.GoldRepository
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.model.GameSettingsLimits
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class GameViewModel(
    private val goldRepository: GoldRepository,
    private val resultRepository: GameResultRepository
) : ViewModel() {

    var state by mutableStateOf<GameState?>(null)
        private set

    private var startedAt: Long? = null

    private var nextId = 1
    private var nextGoldAt = GOLD_INTERVAL_MILLIS
    private var timeUntilBonus = 0f

    private var goldJob: Job? = null
    private var saveJob: Job? = null

    private var roundVersion = 0L

    var fieldWidth by mutableStateOf(0f)
        private set

    var fieldHeight by mutableStateOf(0f)
        private set

    fun ensureGame(
        playerId: Long,
        settings: GameSettings
    ) {
        val current = state

        if (current == null || current.playerId != playerId) {
            startGame(playerId, settings)
        }
    }

    fun restartGame() {
        val current = state ?: return

        if (current.isSaving) return

        startGame(
            playerId = current.playerId,
            settings = current.settings
        )
    }

    fun exitGame() {
        goldJob?.cancel()
        saveJob?.cancel()

        roundVersion++

        state = null
        startedAt = null

        fieldWidth = 0f
        fieldHeight = 0f
    }

    fun initializeField(
        width: Float,
        height: Float
    ) {
        if (!width.isFinite() || !height.isFinite()) return
        if (width < MIN_FIELD_SIZE || height < MIN_FIELD_SIZE) return

        if (fieldWidth > 0f && fieldHeight > 0f) return

        fieldWidth = width
        fieldHeight = height
    }

    fun updateFrame(
        deltaSeconds: Float,
        tilt: Offset
    ) {
        var current = state ?: return

        if (current.finished || current.goldLoading) return
        if (fieldWidth <= 0f || fieldHeight <= 0f) return

        if (startedAt == null) {
            val initialBugs = List(
                current.settings.maxCockroaches
            ) {
                createBug(current.settings)
            }

            current = current.copy(bugs = initialBugs)
            state = current

            startedAt = SystemClock.elapsedRealtime()
        }

        if (!updateTimer()) return

        current = state ?: return

        val elapsed = SystemClock.elapsedRealtime() -
                (startedAt ?: return)

        var bugs = current.bugs

        if (elapsed >= nextGoldAt) {
            val points = current.goldPoints

            if (points != null) {
                val goldBug = BugFactory.createGold(
                    id = nextId++,
                    fieldWidth = fieldWidth,
                    fieldHeight = fieldHeight,
                    speedMultiplier = current.settings.speed,
                    points = points
                )

                bugs = bugs + goldBug
            }

            nextGoldAt =
                (elapsed / GOLD_INTERVAL_MILLIS + 1L) *
                        GOLD_INTERVAL_MILLIS
        }

        val delta = if (deltaSeconds.isFinite()) {
            deltaSeconds.coerceIn(0f, 0.05f)
        } else {
            0f
        }

        var fieldBonus = current.fieldBonus

        timeUntilBonus -= delta

        if (timeUntilBonus <= 0f) {
            fieldBonus = BugFactory.createBonus(
                fieldWidth,
                fieldHeight
            )

            timeUntilBonus += current.settings.bonusInterval
        }

        bugs = bugs.map { bug ->
            bug.move(
                deltaSeconds = delta,
                fieldWidth = fieldWidth,
                fieldHeight = fieldHeight,
                bonusMovement = current.activeBonus?.movement,
                tilt = tilt
            )
        }

        val bonusRemaining = (
                current.bonusRemaining - delta
                ).coerceAtLeast(0f)

        state = current.copy(
            bugs = bugs,
            fieldBonus = fieldBonus,
            activeBonus = if (bonusRemaining > 0f) {
                current.activeBonus
            } else {
                null
            },
            bonusRemaining = bonusRemaining
        )
    }

    fun onTap(
        x: Float,
        y: Float,
        hitScaleX: Float = 1f,
        hitScaleY: Float = 1f
    ) {
        if (!x.isFinite() || !y.isFinite()) return

        if (
            !hitScaleX.isFinite() ||
            !hitScaleY.isFinite() ||
            hitScaleX <= 0f ||
            hitScaleY <= 0f
        ) {
            return
        }

        if (!updateTimer()) return

        val current = state ?: return

        if (x !in 0f..fieldWidth || y !in 0f..fieldHeight) {
            return
        }

        val bonus = current.fieldBonus

        if (
            bonus != null &&
            bonus.contains(
                bonus.x + (x - bonus.x) * hitScaleX,
                bonus.y + (y - bonus.y) * hitScaleY
            )
        ) {
            state = current.copy(
                fieldBonus = null,
                activeBonus = bonus,
                bonusRemaining = bonus.durationSeconds
            )

            return
        }

        val hitBug = current.bugs.lastOrNull { bug ->
            bug.contains(
                bug.x + (x - bug.x) * hitScaleX,
                bug.y + (y - bug.y) * hitScaleY
            )
        }

        if (hitBug == null) {
            state = current.copy(
                score = current.score - MISS_PENALTY,
                misses = current.misses + 1
            )

            return
        }

        val remainingBugs = current.bugs.filterNot {
            it.id == hitBug.id
        }

        val updatedBugs = if (hitBug is GoldBug) {
            remainingBugs
        } else {
            remainingBugs + createBug(current.settings)
        }

        state = current.copy(
            bugs = updatedBugs,
            score = current.score + hitBug.points,
            hits = current.hits + 1
        )
    }

    fun retrySaveResult() {
        saveResult()
    }

    private fun startGame(
        playerId: Long,
        settings: GameSettings
    ) {
        goldJob?.cancel()
        saveJob?.cancel()

        roundVersion++

        val roundSettings = settings.copy(
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

        startedAt = null

        nextId = 1
        nextGoldAt = GOLD_INTERVAL_MILLIS
        timeUntilBonus = roundSettings.bonusInterval.toFloat()

        state = GameState(
            playerId = playerId,
            settings = roundSettings
        )

        loadGoldPrice()
    }

    private fun createBug(settings: GameSettings): Bug {
        return BugFactory.create(
            id = nextId++,
            fieldWidth = fieldWidth,
            fieldHeight = fieldHeight,
            speedMultiplier = settings.speed
        )
    }

    private fun updateTimer(): Boolean {
        val current = state ?: return false
        val start = startedAt ?: return false

        if (current.finished) return false

        val durationMillis =
            current.settings.roundDuration * 1000L

        val elapsed = SystemClock.elapsedRealtime() - start

        val remaining = (
                durationMillis - elapsed
                ).coerceAtLeast(0L)

        val finished = remaining == 0L

        state = current.copy(
            remainingSeconds = (
                    (remaining + 999L) / 1000L
                    ).toInt(),
            finished = finished
        )

        if (finished) {
            saveResult()
        }

        return !finished
    }

    private fun loadGoldPrice() {
        val version = roundVersion

        goldJob = viewModelScope.launch {
            var points: Int? = null
            var error: String? = null

            try {
                val quote = goldRepository.getCached()
                    ?: withTimeout(10_000L) {
                        goldRepository.loadLatest()
                    }

                points = GoldPointsCalculator.calculate(quote)
            } catch (e: TimeoutCancellationException) {
                error = "Не удалось получить цену золота за 10 секунд"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = "Цена золота недоступна"
            }

            // Ответ старой партии не должен изменять новую.
            if (version != roundVersion) return@launch

            val current = state ?: return@launch

            state = current.copy(
                goldLoading = false,
                goldPoints = points,
                goldError = error
            )
        }
    }

    private fun saveResult() {
        val current = state ?: return

        if (
            !current.finished ||
            current.isSaving ||
            current.resultSaved
        ) {
            return
        }

        val version = roundVersion

        state = current.copy(
            isSaving = true,
            saveError = null
        )

        saveJob = viewModelScope.launch {
            try {
                resultRepository.saveResult(
                    playerId = current.playerId,
                    score = current.score,
                    settings = current.settings
                )

                if (version != roundVersion) return@launch

                val latest = state ?: return@launch

                state = latest.copy(
                    isSaving = false,
                    resultSaved = true
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (version != roundVersion) return@launch

                val latest = state ?: return@launch

                state = latest.copy(
                    isSaving = false,
                    saveError = "Не удалось сохранить результат"
                )
            }
        }
    }

    private companion object {
        const val MISS_PENALTY = 1
        const val GOLD_INTERVAL_MILLIS = 20_000L
        const val MIN_FIELD_SIZE = 140f
    }
}