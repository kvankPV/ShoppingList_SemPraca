package com.example.shoppinglist_sempraca.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.shoppinglist_sempraca.ShopListApplication
import com.example.shoppinglist_sempraca.ui.home.HomeViewModel
import com.example.shoppinglist_sempraca.ui.home.ItemEntryViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        // Ini for HomeViewModel
        initializer {
            HomeViewModel(shopListApplication().container.itemsRepository)
        }
        // Ini for EntryViewModel
        initializer {
            ItemEntryViewModel(shopListApplication().container.itemsRepository)
        }
    }
}

fun CreationExtras.shopListApplication(): ShopListApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShopListApplication)