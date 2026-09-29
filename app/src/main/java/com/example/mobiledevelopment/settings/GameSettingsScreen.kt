package com.example.mobiledevelopment.settings
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.model.GameSettingsLimits
import kotlin.math.roundToInt
private val bonusPresets = listOf(5, 10, 15, 30, 60)
@Composable
fun GameSettingsScreen(
    settings: GameSettings,
    onSettingsChange: (GameSettings) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium
        )
        SettingsCard(stringResource(R.string.settings_speed)) {
            SpeedPicker(
                value = settings.speed,
                onChange = {
                    onSettingsChange(settings.copy(speed = it))
                }
            )
        }
        SettingsCard(stringResource(R.string.settings_max_cockroaches)) {
            CockroachCounter(
                value = settings.maxCockroaches,
                onChange = {
                    onSettingsChange(settings.copy(maxCockroaches = it))
                }
            )
        }
        SettingsCard(stringResource(R.string.settings_bonus_interval)) {
            BonusIntervalPicker(
                value = settings.bonusInterval,
                onChange = {
                    onSettingsChange(settings.copy(bonusInterval = it))
                }
            )
        }
        SettingsCard(stringResource(R.string.settings_round_duration)) {
            RoundDurationPicker(
                value = settings.roundDuration,
                onChange = {
                    onSettingsChange(settings.copy(roundDuration = it))
                }
            )
        }
        OutlinedButton(
            onClick = { onSettingsChange(GameSettings()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.settings_reset))
        }
    }
}
@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}
@Composable
private fun SpeedPicker(
    value: Int,
    onChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.settings_multiplier, value))
                Text("▾")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            GameSettingsLimits.speed.forEach { speed ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                R.string.settings_multiplier,
                                speed
                            ),
                            color = if (speed == value) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onChange(speed)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
private fun CockroachCounter(
    value: Int,
    onChange: (Int) -> Unit
) {
    val range = GameSettingsLimits.maxCockroaches
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = { onChange(value - 1) },
            enabled = value > range.first,
            modifier = Modifier.size(56.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("−", style = MaterialTheme.typography.headlineSmall)
        }
        Text(
            text = stringResource(R.string.settings_count, value),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        OutlinedButton(
            onClick = { onChange(value + 1) },
            enabled = value < range.last,
            modifier = Modifier.size(56.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
@Composable
private fun BonusIntervalPicker(
    value: Int,
    onChange: (Int) -> Unit
) {
    val options = (bonusPresets + value)
        .filter { it in GameSettingsLimits.bonusInterval }
        .distinct()
        .sorted()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { interval ->
            FilterChip(
                selected = value == interval,
                onClick = { onChange(interval) },
                label = {
                    Text(stringResource(R.string.settings_seconds, interval))
                }
            )
        }
    }
}
@Composable
private fun RoundDurationPicker(
    value: Int,
    onChange: (Int) -> Unit
) {
    val range = GameSettingsLimits.roundDuration
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.settings_seconds, value),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Slider(
            value = value.toFloat(),
            onValueChange = {
                onChange(it.roundToInt().coerceIn(range))
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_seconds, range.first),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = stringResource(R.string.settings_seconds, range.last),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}