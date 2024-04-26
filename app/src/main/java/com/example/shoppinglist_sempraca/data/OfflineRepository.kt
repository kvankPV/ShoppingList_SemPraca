package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
[OfflineRepository] is responsible for managing the data operations related to [Item] and [Product] entities.
It uses the Data Access Objects (DAOs) [itemDao] and [productDao] to interact with the SQLite database.
 */
class OfflineRepository(private val itemDao:ItemDAO, private val productDao: ProductDAO): Repository {
    override suspend fun insertItem(item: Item) = itemDao.insertItem(item)
    override suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)
    override suspend fun updateItem(item: Item) = itemDao.updateItem(item)
    override fun getItemStream(idItem: Int): Flow<Item?> = itemDao.getItem(idItem)
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()
    override suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    override suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    override suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    override fun getProductStream(idProduct: Int, idItem: Int): Flow<Product> = productDao.getProduct(idProduct, idItem)
    override fun getAllProductsStream(): Flow<List<Product>> = productDao.getAllProducts()
    override fun getAllProductsFromItemStream(idItem: Int): Flow<List<Product>> = productDao.getAllProductsFromItem(idItem)

    override suspend fun updateItemVisibilityBasedOnProducts(itemId: Int) {
        val products = getAllProductsFromItemStream(itemId).first()
        if (products.isNotEmpty() && products.all { it.checkedOut }) {
            val item = getItemStream(itemId).first()
            if (item != null) {
                val updatedItem = item.copy(isVisible = false)
                updateItem(updatedItem)
            }
        }
    }
}