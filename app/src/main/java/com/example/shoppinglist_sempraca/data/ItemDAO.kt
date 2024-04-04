package com.example.shoppinglist_sempraca.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * from items WHERE name = :name")
    fun getItem(name: String): Flow<Item>

    @Query("SELECT * from items")
    fun getAllItems(): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * from products WHERE id = :id and itemName = :itemName")
    fun getProduct(id: Int, itemName: String): Flow<Product>

    @Query("SELECT * from products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * from products WHERE itemName = :itemName")
    fun getAllProductsFromItem(itemName: String): Flow<List<Product>>
}