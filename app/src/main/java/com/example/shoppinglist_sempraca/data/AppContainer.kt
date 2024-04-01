package com.example.shoppinglist_sempraca.data

import android.content.Context

interface AppContainer {
    val itemsRepository: ItemsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(ItemDatabase.getDatabase(context).itemDao())
    }
}