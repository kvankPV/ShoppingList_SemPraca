package com.example.shoppinglist_sempraca.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
* Entity data class represents a single row in the database with a
* foreign key to reference value [itemId] with the table "items".
 */
@Entity(tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = ["itemId"],
        childColumns = ["itemId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("itemId")]
)
data class Product (
    @PrimaryKey(autoGenerate = true)
    val productId: Int,
    val itemId: Int, // This will be the foreign key
    val productName: String,
    val productCategory: String,
    val productQuantity: Int,
    val productPrice: Double,
    val productCheckedOut: Boolean
)