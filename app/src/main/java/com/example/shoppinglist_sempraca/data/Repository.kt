package com.example.shoppinglist_sempraca.data

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] and [Product]
 * from a given data source.
 */
interface Repository {
    suspend fun <T> insertStream(entity: T)
    suspend fun <T> deleteStream(entity: T)
    suspend fun <T> updateStream(entity: T)
    fun getItemStream(idItem: Int) : Flow<Item?>
    fun getVisibleItemsStream(): Flow<PagingData<Item>>
    fun getNonVisibleItemsStream(): Flow<PagingData<Item>>
    fun getAllProductsFromItemStream(idItem: Int): Flow<PagingData<Product>>
    fun getAllPricesFromNonVisibleItemsStream(): Flow<List<Double>>
    suspend fun countAllCheckedOutProductsFromItemStream(idItem: Int): Int
    suspend fun countAllProductsFromItemStream(idItem: Int): Int
    suspend fun countItemsWithTotalStream(): Int
    suspend fun countNonVisibleItemsStream(): Int
}