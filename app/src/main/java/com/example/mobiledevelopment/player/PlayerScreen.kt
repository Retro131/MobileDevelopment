package com.example.mobiledevelopment.player

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mobiledevelopment.data.PlayerRepository
import com.example.mobiledevelopment.model.Player
import com.example.mobiledevelopment.registration.RegistrationConfig
import com.example.mobiledevelopment.registration.RegistrationScreen
import com.example.mobiledevelopment.session.SessionPlayer
import com.example.mobiledevelopment.session.SessionViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private enum class PlayerEntryPage {
    CHOICE,
    PROFILES,
    REGISTRATION
}

@Composable
fun PlayerScreen(
    session: SessionViewModel,
    repository: PlayerRepository,
    onStartGame: () -> Unit
) {
    val currentPlayer by
    session.currentPlayer.collectAsStateWithLifecycle()

    val playersFlow = remember(repository) {
        repository.observeRegisteredPlayers()
    }

    val players by playersFlow.collectAsStateWithLifecycle(
        initialValue = emptyList()
    )

    var entryPage by rememberSaveable {
        mutableStateOf(PlayerEntryPage.CHOICE)
    }

    var isCreatingGuest by remember {
        mutableStateOf(false)
    }

    var guestError by remember {
        mutableStateOf<String?>(null)
    }

    val scope = rememberCoroutineScope()
    val config = remember { RegistrationConfig() }

    fun selectPlayer(player: Player) {
        session.selectPlayer(
            SessionPlayer(
                id = player.id,
                name = player.fullName,
                isGuest = player.isGuest
            )
        )

        entryPage = PlayerEntryPage.CHOICE
        guestError = null
    }

    val player = currentPlayer

    if (player != null) {
        PlayerMenu(
            player = player,
            onStartGame = onStartGame,
            onLogout = {
                entryPage = PlayerEntryPage.CHOICE
                guestError = null
                session.clearPlayer()
            }
        )
        return
    }

    when (entryPage) {
        PlayerEntryPage.CHOICE -> {
            LoginChoice(
                isCreatingGuest = isCreatingGuest,
                error = guestError,
                onChoosePlayer = {
                    guestError = null
                    entryPage = PlayerEntryPage.PROFILES
                },
                onGuestLogin = {
                    if (!isCreatingGuest) {
                        isCreatingGuest = true
                        guestError = null

                        scope.launch {
                            try {
                                val guest = repository.createGuest()
                                selectPlayer(guest)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                guestError = "Не удалось создать гостя"
                            } finally {
                                isCreatingGuest = false
                            }
                        }
                    }
                }
            )
        }

        PlayerEntryPage.PROFILES -> {
            PlayerSelectionScreen(
                players = players,
                onSelectPlayer = { selectedPlayer ->
                    selectPlayer(selectedPlayer)
                },
                onRegister = {
                    entryPage = PlayerEntryPage.REGISTRATION
                },
                onBack = {
                    entryPage = PlayerEntryPage.CHOICE
                }
            )
        }

        PlayerEntryPage.REGISTRATION -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Button(
                    onClick = {
                        entryPage = PlayerEntryPage.PROFILES
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Назад")
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    RegistrationScreen(
                        config = config,
                        onRegister = { candidate ->
                            val savedPlayer =
                                repository.register(candidate)

                            selectPlayer(savedPlayer)

                            savedPlayer
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerMenu(
    player: SessionPlayer,
    onStartGame: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Текущий игрок: ${player.name}")

        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Играть")
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти из профиля")
        }
    }
}

@Composable
private fun LoginChoice(
    isCreatingGuest: Boolean,
    error: String?,
    onChoosePlayer: () -> Unit,
    onGuestLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onChoosePlayer,
            enabled = !isCreatingGuest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти / Зарегистрироваться")
        }

        Button(
            onClick = onGuestLogin,
            enabled = !isCreatingGuest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isCreatingGuest) {
                    "Создание гостя…"
                } else {
                    "Войти как гость"
                }
            )
        }

        error?.let { message ->
            Text(message)
        }
    }
}