package com.example.shoppinglist_sempraca

import android.app.Application
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shoppinglist_sempraca.data.AppContainer
import com.example.shoppinglist_sempraca.data.AppDataContainer
import java.util.concurrent.TimeUnit

class ShopListApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        val workRequest = PeriodicWorkRequestBuilder<CheckWorker>(1, TimeUnit.DAYS).build()
        Configuration.Builder()
            .setWorkerFactory(CheckWorkerFactory(container))
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}