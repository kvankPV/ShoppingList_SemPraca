package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao:ItemDAO): ItemsRepository {
    override suspend fun insertItem(item: Item) = itemDao.insert(item)
    override suspend fun deleteItem(item: Item) = itemDao.delete(item)
    override suspend fun updateItem(item: Item) = itemDao.update(item)
    override fun getItemStream(name: String): Flow<Item?> = itemDao.getItem(name)
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()
    override suspend fun insertProduct(product: Product) = itemDao.insertProduct(product)
    override suspend fun updateProduct(product: Product) = itemDao.updateProduct(product)
    override suspend fun deleteProduct(product: Product) = itemDao.deleteProduct(product)
    override fun getProductStream(id: Int, itemName: String): Flow<Product> = itemDao.getProduct(id, itemName)
    override fun getAllProductsStream(): Flow<List<Product>> = itemDao.getAllProducts()
    override fun getAllProductsFromItemStream(itemName: String): Flow<List<Product>> = itemDao.getAllProductsFromItem(itemName)
}