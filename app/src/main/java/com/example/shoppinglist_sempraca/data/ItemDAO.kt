package com.example.shoppinglist_sempraca.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ItemDAO {
    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(item: Item)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("SELECT * from items WHERE itemId = :itemId")
    fun getItem(itemId: Int): Flow<Item>

    @Query("SELECT itemTotalPrice from items where itemVisibility = 0")
    fun getAllPricesFromNonVisible(): Flow<List<Double>>

    @Query("SELECT * FROM items WHERE itemVisibility = 1")
    fun getVisibleItems(): PagingSource<Int, Item>

    @Query("SELECT * FROM items WHERE itemVisibility = 0")
    fun getNonVisibleItems(): PagingSource<Int, Item>

    @Query("select count(*) from items where itemTotalPrice > 0.0")
    fun getCountItemsWithTotal(): Int

    @Query("select count(*) from items where itemVisibility = 0")
    fun getCountOfNonVisibleItems(): Int
}