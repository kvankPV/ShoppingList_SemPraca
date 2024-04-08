package com.example.shoppinglist_sempraca.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val isVisible: Boolean = true,
    val totalPrice: Double
)