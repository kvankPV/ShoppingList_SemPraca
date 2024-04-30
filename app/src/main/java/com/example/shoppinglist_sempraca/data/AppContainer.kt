package com.example.shoppinglist_sempraca.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val repository: Repository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [Repository]
     */
    override val repository: Repository by lazy {
        OfflineRepository(AppDatabase.getDatabase(context).itemDAO(),AppDatabase.getDatabase(context).productDAO())
    }
}