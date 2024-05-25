package com.example.shoppinglist_sempraca

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.shoppinglist_sempraca.data.AppContainer
import com.example.shoppinglist_sempraca.data.Repository
import java.util.concurrent.TimeUnit

class CheckWorkerFactory(private val appContainer: AppContainer): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): CoroutineWorker? {
        return when (workerClassName) {
            CheckWorker::class.java.name ->
                CheckWorker(appContext, workerParameters, appContainer.repository)
            else -> null
        }
    }
}
class CheckWorker(appContext: Context, workerParams: WorkerParameters, private val repository: Repository) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("CheckWorker", Context.MODE_PRIVATE)
        val lastDailyReminderDate = sharedPreferences.getLong("lastDailyReminderDate", 0)
        val lastWeeklyReminderDate = sharedPreferences.getLong("lastWeeklyReminderDate", 0)
        val currentDate = System.currentTimeMillis()

        val numberOfItemsWithTotal = repository.countItemsWithTotalStream()
        val numberOfArchivedItems = repository.countNonVisibleItemsStream()

        if (numberOfItemsWithTotal <= numberOfArchivedItems && currentDate != lastDailyReminderDate) {
            val notificationHelper = NotificationHelper(applicationContext)
            val subtractionPom = numberOfArchivedItems - numberOfItemsWithTotal
            notificationHelper.createNotification("Reminder", "Please enter prices for your products in $subtractionPom items ! ", 1)
            sharedPreferences.edit().putLong("lastDailyReminderDate", currentDate).apply()
            return Result.success()
        }

        if (numberOfItemsWithTotal > 5 && currentDate - lastWeeklyReminderDate >= TimeUnit.DAYS.toMillis(7)) {
            val notificationHelper = NotificationHelper(applicationContext)
            notificationHelper.createNotification("Reminder", "Check your shopping trends!", 2)
            sharedPreferences.edit().putLong("lastWeeklyReminderDate", currentDate).apply()
            return Result.success()
        }

        return Result.success()
    }
}