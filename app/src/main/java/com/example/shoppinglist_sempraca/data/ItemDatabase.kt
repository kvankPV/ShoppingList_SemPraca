package com.example.shoppinglist_sempraca.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
//Version 1, in Item data class Item I had chosen the wrong PK.
@Database(entities = [Item::class, Product::class], version = 2, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDAO(): ItemDAO
    abstract fun productDAO(): ProductDAO

    companion object {
        @Volatile
        private var Instance: ItemDatabase? = null

        fun getDatabase(context: Context): ItemDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ItemDatabase::class.java,
                    "item_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {Instance = it}
            }
        }
    }
}