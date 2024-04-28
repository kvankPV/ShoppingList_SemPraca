package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
[OfflineRepository] is responsible for managing the data operations related to [Item] and [Product] entities.
It uses the Data Access Objects (DAOs) [itemDao] and [productDao] to interact with the SQLite database.
 */
class OfflineRepository(private val itemDao:ItemDAO, private val productDao: ProductDAO): Repository {
    override suspend fun <T> insert(entity: T) {
        when (entity) {
            is Item -> itemDao.insertItem(entity)
            is Product -> productDao.insertProduct(entity)
        }
    }

    override suspend fun <T> update(entity: T) {
        when (entity) {
            is Item -> itemDao.updateItem(entity)
            is Product -> productDao.updateProduct(entity)
        }
    }

    override suspend fun <T> delete(entity: T) {
        when (entity) {
            is Item -> itemDao.deleteItem(entity)
            is Product -> productDao.deleteProduct(entity)
        }
    }
    override fun getItemStream(idItem: Int): Flow<Item?> = itemDao.getItem(idItem)
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()
    override fun getAllProductsFromItemStream(idItem: Int): Flow<List<Product>> = productDao.getAllProductsFromItem(idItem)
    override fun getAllPricesFromNonVisibleItems(): Flow<List<Double>> = itemDao.getAllPricesFromNonVisible()

    override suspend fun updateItemVisibilityBasedOnProducts(itemId: Int) {
        val products = getAllProductsFromItemStream(itemId).first()
        if (products.isNotEmpty() && products.all { it.productCheckedOut }) {
            val item = getItemStream(itemId).first()
            if (item != null) {
                val updatedItem = item.copy(itemVisibility = false)
                update(updatedItem)
            }
        }
    }
}