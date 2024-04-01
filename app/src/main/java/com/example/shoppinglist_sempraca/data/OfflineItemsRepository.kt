package com.example.shoppinglist_sempraca.data

import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao:ItemDAO): ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()
    override fun getItemStream(name: String): Flow<Item?> = itemDao.getItem(name)
    override suspend fun insertItem(item: Item) = itemDao.insert(item)
    override suspend fun deleteItem(item: Item) = itemDao.delete(item)
    override suspend fun updateItem(item: Item) = itemDao.update(item)
}