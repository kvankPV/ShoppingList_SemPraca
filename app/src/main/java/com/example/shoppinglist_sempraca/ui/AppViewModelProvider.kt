package com.example.shoppinglist_sempraca.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.shoppinglist_sempraca.ShopListApplication
import com.example.shoppinglist_sempraca.ui.home.HomeViewModel
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app.
 */
object AppViewModelProvider {
    val factory = viewModelFactory {
        // Ini for HomeViewModel
        initializer {
            HomeViewModel(shopListApplication().container.itemsRepository)
        }
        //Ini for ManipulationViewModel
        initializer {
            ItemManipulationViewModel(shopListApplication().container.itemsRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ShopListApplication].
 */
fun CreationExtras.shopListApplication(): ShopListApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShopListApplication)