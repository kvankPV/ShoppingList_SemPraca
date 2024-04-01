package com.example.shoppinglist_sempraca

import android.app.Application
import com.example.shoppinglist_sempraca.data.AppContainer
import com.example.shoppinglist_sempraca.data.AppDataContainer

class ShopListApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }

}