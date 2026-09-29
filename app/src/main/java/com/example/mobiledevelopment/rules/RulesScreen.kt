package com.example.mobiledevelopment.rules
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mobiledevelopment.R
@Composable
fun RulesScreen() {
    val context = LocalContext.current
    val text = remember(context) {
        val html = context.resources.openRawResource(R.raw.rules)
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    }
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        factory = { context ->
            TextView(context).apply {
                textSize = 18f
            }
        },
        update = { view ->
            view.text = text
            view.setTextColor(textColor)
        }
    )
}