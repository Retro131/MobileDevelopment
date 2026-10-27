package com.example.mobiledevelopment.gold

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import java.time.format.DateTimeFormatter

@Composable
fun GoldPriceScreen() {
    val repository = remember { GoldRepository() }

    var quote by remember {
        mutableStateOf<GoldQuote?>(null)
    }

    var loading by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var requestNumber by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(requestNumber) {
        loading = true
        errorMessage = null

        try {
            quote = repository.loadLatest()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            errorMessage =
                e.message ?: "Не удалось загрузить цену золота"
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Золото ЦБ",
            style = MaterialTheme.typography.headlineSmall
        )

        if (loading) {
            Text("Загружаем...")
        }

        quote?.let { value ->
            Text(
                text = "${value.price.toPlainString()} ₽/г"
            )

            Text(
                text = "Дата: ${
                    value.date.format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    )
                }"
            )
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = { requestNumber++ },
            enabled = !loading
        ) {
            Text("Обновить")
        }
    }
}