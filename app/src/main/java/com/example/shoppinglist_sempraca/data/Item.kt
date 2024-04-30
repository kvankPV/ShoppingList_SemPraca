package com.example.shoppinglist_sempraca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item (
    @PrimaryKey(autoGenerate = true)
    val itemId: Int,
    val itemName: String,
    val itemVisibility: Boolean = true,
    val itemTotalPrice: Double
)