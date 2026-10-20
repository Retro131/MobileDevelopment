package com.example.mobiledevelopment.registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.model.Player
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun RegistrationScreen(
    config: RegistrationConfig,
    onRegister: suspend (Player) -> Player
) {
    val coroutineScope = rememberCoroutineScope()

    var isSaving by remember {
        mutableStateOf(false)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var form by rememberSaveable {
        mutableStateOf(RegistrationState.initial(config))
    }
    var player by rememberSaveable { mutableStateOf<Player?>(null) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Регистрация игрока",
            style = MaterialTheme.typography.headlineMedium
        )
        OutlinedTextField(
            value = form.fullName,
            onValueChange = { form = form.copy(fullName = it) },
            label = { Text("ФИО") },
            singleLine = true,
            isError = submitted && !form.isValid,
            modifier = Modifier.fillMaxWidth()
        )
        if (submitted && !form.isValid) {
            Text("Введите ФИО", color = MaterialTheme.colorScheme.error)
        }
        GenderPicker(
            value = form.gender,
            onChange = { form = form.copy(gender = it) }
        )
        CoursePicker(
            value = form.course,
            courses = config.courses,
            onChange = { form = form.copy(course = it) }
        )
        BirthDatePicker(
            value = form.birthDate,
            minDate = config.earliestBirthDate,
            onChange = { form = form.copy(birthDate = it) }
        )
        Button(
            onClick = {
                submitted = true

                if (form.isValid && !isSaving && player == null) {
                    val newPlayer = form.toPlayer()

                    isSaving = true
                    errorMessage = null

                    coroutineScope.launch {
                        try {
                            player = onRegister(newPlayer)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            errorMessage = e.message
                                ?: "Ошибка регистрации"
                        } finally {
                            isSaving = false
                        }
                    }
                }
            },
            enabled = !isSaving && player == null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                when {
                    isSaving -> "Сохраняем..."
                    player != null -> "Пользователь зарегистрирован"
                    else -> "Зарегистрироваться"
                }
            )
        }
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
        player?.let { PlayerCard(it) }
        if (player != null) {
            OutlinedButton(
                onClick = {
                    player = null
                    form = RegistrationState.initial(config)
                    submitted = false
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрировать другого")
            }
        }
    }
}