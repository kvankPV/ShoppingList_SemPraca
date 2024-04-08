package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] and [Product]
 * from a given data source.
 */
interface ItemsRepository {
    suspend fun insertItem(item: Item)
    suspend fun deleteItem(item: Item)
    suspend fun updateItem(item: Item)
    fun getItemStream(idItem: Int) : Flow<Item?>
    fun getAllItemsStream(): Flow<List<Item>>
    suspend fun insertProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    fun getProductStream(idProduct: Int, idItem: Int): Flow<Product>
    fun getAllProductsStream(): Flow<List<Product>>
    fun getAllProductsFromItemStream(idItem: Int): Flow<List<Product>>
}