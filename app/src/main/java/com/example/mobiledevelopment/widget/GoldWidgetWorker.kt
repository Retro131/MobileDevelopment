package com.example.mobiledevelopment.widget

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobiledevelopment.gold.GoldQuoteStorage
import com.example.mobiledevelopment.gold.GoldRepository
import kotlinx.coroutines.CancellationException

class GoldWidgetWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("GoldWidget", "Worker запущен")

        val repository = GoldRepository(
            storage = GoldQuoteStorage(applicationContext)
        )

        var quote = repository.getCached()

        GoldWidgetProvider.render(
            context = applicationContext,
            quote = quote,
        )

        return try {
            quote = repository.loadLatest()

            GoldWidgetProvider.render(
                context = applicationContext,
                quote = quote,
            )

            Result.success()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            GoldWidgetProvider.render(
                context = applicationContext,
                quote = quote,
            )

            Result.failure()
        }
    }
}