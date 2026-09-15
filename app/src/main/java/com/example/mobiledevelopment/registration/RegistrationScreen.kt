package com.example.mobiledevelopment.registration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.model.Player

@Composable
fun RegistrationScreen(config: RegistrationConfig) {
    var form by rememberSaveable {
        mutableStateOf(RegistrationState.initial(config))
    }
    var player by rememberSaveable { mutableStateOf<Player?>(null) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
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
        DifficultyPicker(
            value = form.difficulty,
            range = config.difficulties,
            onChange = { form = form.copy(difficulty = it) }
        )
        BirthDatePicker(
            value = form.birthDate,
            minDate = config.earliestBirthDate,
            onChange = { form = form.copy(birthDate = it) }
        )
        Button(
            onClick = {
                submitted = true
                player = if (form.isValid) form.toPlayer() else null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }
        player?.let { PlayerCard(it) }
    }
}