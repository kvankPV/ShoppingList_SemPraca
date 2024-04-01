package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow

interface ItemsRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllItemsStream(): Flow<List<Item>>

    /**
     * Retrieve the item with the given name from the given data source.
     */
    fun getItemStream(name: String) : Flow<Item?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Item)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Item)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Item)
}