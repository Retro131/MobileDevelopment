package com.example.mobiledevelopment.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.work.WorkManager
import com.example.mobiledevelopment.R
import com.example.mobiledevelopment.gold.GoldQuote
import java.math.RoundingMode
import java.time.format.DateTimeFormatter
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import java.util.concurrent.TimeUnit

class GoldWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(
            "GoldWidget",
            "onUpdate: ${appWidgetIds.joinToString()}"
        )

        enqueueUpdate(context)
    }

    companion object {

        private const val WORK_NAME = "gold_widget_daily"

        private fun enqueueUpdate(context: Context) {
            val request =
                PeriodicWorkRequestBuilder<GoldWidgetWorker>(
                    24,
                    TimeUnit.HOURS
                ).build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }

        fun render(
            context: Context,
            quote: GoldQuote?,
        ) {
            val manager = AppWidgetManager.getInstance(context)

            val widgetIds = manager.getAppWidgetIds(
                ComponentName(
                    context,
                    GoldWidgetProvider::class.java
                )
            )

            if (widgetIds.isEmpty()) return


            val views = RemoteViews(
                context.packageName,
                R.layout.widget_gold
            )

            val priceText = quote?.price
                ?.setScale(2, RoundingMode.HALF_UP)
                ?.toPlainString()
                ?.replace('.', ',')
                ?: "—"

            val dateText = quote?.date?.format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy")
            ) ?: "Нет данных"

            views.setTextViewText(
                R.id.gold_widget_price,
                priceText
            )

            views.setTextViewText(
                R.id.gold_widget_date,
                dateText
            )

            manager.updateAppWidget(widgetIds, views)
        }
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }
}

