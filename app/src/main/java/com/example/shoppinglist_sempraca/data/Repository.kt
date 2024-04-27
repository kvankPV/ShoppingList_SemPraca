package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] and [Product]
 * from a given data source.
 */
interface Repository {
    suspend fun <T> insert(entity: T)
    suspend fun <T> delete(entity: T)
    suspend fun <T> update(entity: T)
    fun getItemStream(idItem: Int) : Flow<Item?>
    fun getAllItemsStream(): Flow<List<Item>>
    fun getProductStream(idProduct: Int, idItem: Int): Flow<Product>
    fun getAllProductsStream(): Flow<List<Product>>
    fun getAllProductsFromItemStream(idItem: Int): Flow<List<Product>>
    suspend fun updateItemVisibilityBasedOnProducts(itemId: Int)
}