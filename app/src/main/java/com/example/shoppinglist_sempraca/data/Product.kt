package com.example.shoppinglist_sempraca.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
* Entity data class represents a single row in the database with a
* foreign key to reference value [idItem] with the table "items".
 */
@Entity(tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = ["id"],
        childColumns = ["idItem"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Product (
    @PrimaryKey(autoGenerate = true)
    val idProduct: Int,
    val idItem: Int, // This will be the foreign key
    val name: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val checkedOut: Boolean
)