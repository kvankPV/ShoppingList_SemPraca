package com.example.shoppinglist_sempraca

import android.app.Application
import com.example.shoppinglist_sempraca.data.AppContainer
import com.example.shoppinglist_sempraca.data.AppDataContainer

class ShopListApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}