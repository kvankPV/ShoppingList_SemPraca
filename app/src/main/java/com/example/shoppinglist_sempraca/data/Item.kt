package com.example.shoppinglist_sempraca.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Item::class,
        parentColumns = ["name"],
        childColumns = ["itemName"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val itemName: String, // This will be the foreign key
    val name: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val checkedOut: Boolean
)

@Entity(tableName = "items")
data class Item (
    @PrimaryKey
    val name: String,
    val isVisible: Boolean = true,
    val totalPrice: Double
)