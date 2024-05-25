package com.example.shoppinglist_sempraca.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
[OfflineRepository] is responsible for managing the data operations related to [Item] and [Product] entities.
It uses the Data Access Objects (DAOs) [itemDao] and [productDao] to interact with the SQLite database.
 */
class OfflineRepository(private val itemDao:ItemDAO, private val productDao: ProductDAO): Repository {
    override suspend fun <T> insertStream(entity: T) {
        when (entity) {
            is Item -> itemDao.insertItem(entity)
            is Product -> productDao.insertProduct(entity)
        }
    }

    override suspend fun <T> updateStream(entity: T) {
        when (entity) {
            is Item -> itemDao.updateItem(entity)
            is Product -> productDao.updateProduct(entity)
        }
    }

    override suspend fun <T> deleteStream(entity: T) {
        when (entity) {
            is Item -> itemDao.deleteItem(entity)
            is Product -> productDao.deleteProduct(entity)
        }
    }

    override fun getItemStream(idItem: Int): Flow<Item?> = itemDao.getItem(idItem)
    override fun getAllItemsStream(): Flow<PagingData<Item>> {
        return Pager(PagingConfig(pageSize = 20)) {
            itemDao.getAllItems()
        }.flow
    }

    override fun getVisibleItemsStream(): Flow<PagingData<Item>> {
        return Pager(PagingConfig(pageSize = 50)) {
            itemDao.getVisibleItems()
        }.flow
    }

    override fun getNonVisibleItemsStream(): Flow<PagingData<Item>> {
        return Pager(PagingConfig(pageSize = 50)) {
            itemDao.getNonVisibleItems()
        }.flow
    }


    override fun getAllProductsFromItemStream(idItem: Int): Flow<PagingData<Product>> {
        return Pager(PagingConfig(pageSize = 20)) {
            productDao.getAllProductsFromItem(idItem)
        }.flow
    }

    override fun getAllPricesFromNonVisibleItemsStream(): Flow<List<Double>> =
        itemDao.getAllPricesFromNonVisible()

    override suspend fun countAllCheckedOutProductsFromItemStream(idItem: Int): Int {
        return productDao.countCheckedOutProductsFromItem(idItem)
    }

    override suspend fun countAllProductsFromItemStream(idItem: Int): Int {
        return productDao.countAllProductsFromItem(idItem)
    }

}