package com.example.mobiledevelopment.registration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.CalendarView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mobiledevelopment.model.Gender
import com.example.mobiledevelopment.model.Player
import com.example.mobiledevelopment.model.Zodiac
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.math.roundToInt
import androidx.core.graphics.createBitmap

private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
@Composable
fun GenderPicker(value: Gender, onChange: (Gender) -> Unit) {
    Column {
        Text("Пол")
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Gender.entries.forEach { gender ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = value == gender,
                            onClick = { onChange(gender) },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = value == gender,
                        onClick = null
                    )
                    Text(gender.title, Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
@Composable
fun CoursePicker(
    value: Int,
    courses: IntRange,
    onChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("Курс: $value ▾")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = { Text("$course курс") },
                    onClick = {
                        onChange(course)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun DifficultyPicker(
    value: Int,
    range: IntRange,
    onChange: (Int) -> Unit
) {
    Column {
        Text("Уровень сложности: $value")
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.roundToInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = range.last - range.first - 1
        )
    }
}
@Composable
fun BirthDatePicker(
    value: LocalDate,
    minDate: LocalDate,
    onChange: (LocalDate) -> Unit
) {
    val currentOnChange by rememberUpdatedState(onChange)
    val format = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    Column {
        Text("Дата рождения: ${value.format(format)}")
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp),
            factory = { context ->
                CalendarView(context).apply {
                    this.minDate = minDate.toCalendarMillis()
                    maxDate = System.currentTimeMillis()
                    date = value.toCalendarMillis()
                    setOnDateChangeListener { _, year, month, day ->
                        currentOnChange(LocalDate.of(year, month + 1, day))
                    }
                }
            },
            update = { view ->
                view.minDate = minDate.toCalendarMillis()
                val shown = Calendar.getInstance().apply {
                    timeInMillis = view.date
                }
                val shownDate = LocalDate.of(
                    shown.get(Calendar.YEAR),
                    shown.get(Calendar.MONTH) + 1,
                    shown.get(Calendar.DAY_OF_MONTH)
                )
                if (shownDate != value) {
                    view.setDate(value.toCalendarMillis(), false, true)
                }
            }
        )
    }
}
fun LocalDate.toCalendarMillis(): Long {
    return Calendar.getInstance().apply {
        clear()
        set(
            this@toCalendarMillis.year,
            this@toCalendarMillis.monthValue - 1,
            this@toCalendarMillis.dayOfMonth
        )
    }.timeInMillis
}
@Composable
fun ZodiacImage(zodiac: Zodiac, modifier: Modifier = Modifier) {
    val bitmap = remember(zodiac) {
        val image = createBitmap(256, 256)
        val canvas = Canvas(image)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(103, 58, 183)
            textSize = 180f
            textAlign = Paint.Align.CENTER
        }
        val y = image.height / 2f - (paint.ascent() + paint.descent()) / 2f
        canvas.drawText(zodiac.symbol, image.width / 2f, y, paint)
        image.asImageBitmap()
    }
    Image(
        bitmap = bitmap,
        contentDescription = zodiac.title,
        modifier = modifier
    )
}
@Composable
fun PlayerCard(player: Player) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Данные игрока", style = MaterialTheme.typography.titleLarge)
            Text(
                """
                    ФИО: ${player.fullName}
                    Пол: ${player.gender.title}
                    Курс: ${player.course}
                    Сложность: ${player.difficulty}
                    Дата рождения: ${player.birthDate.format(dateFormat)}
                    Знак зодиака: ${player.zodiac.title}
                """.trimIndent()
            )
            ZodiacImage(
                zodiac = player.zodiac,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}