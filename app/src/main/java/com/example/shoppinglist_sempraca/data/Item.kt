package com.example.shoppinglist_sempraca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Product (
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
    val products: ArrayList<Product>
)