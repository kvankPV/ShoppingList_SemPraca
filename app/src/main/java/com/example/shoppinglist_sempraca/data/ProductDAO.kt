package com.example.shoppinglist_sempraca.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * from products WHERE itemId = :itemId")
    fun getAllProductsFromItem(itemId: Int): PagingSource<Int, Product>

    @Query("SELECT COUNT(*) from products WHERE itemId = :itemId and productCheckedOut = 1")
    suspend fun countCheckedOutProductsFromItem(itemId: Int): Int

    @Query("SELECT COUNT(*) from products WHERE itemId = :itemId")
    suspend fun countAllProductsFromItem(itemId: Int): Int
}