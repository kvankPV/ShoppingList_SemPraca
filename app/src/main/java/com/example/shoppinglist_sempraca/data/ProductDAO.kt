package com.example.shoppinglist_sempraca.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * from products WHERE idProduct = :idProduct and idItem = :idItem")
    fun getProduct(idProduct: Int, idItem: Int): Flow<Product>

    @Query("SELECT * from products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * from products WHERE idItem = :idItem")
    fun getAllProductsFromItem(idItem: Int): Flow<List<Product>>
}