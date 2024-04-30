package com.example.shoppinglist_sempraca.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.shoppinglist_sempraca.ShopListApplication
import com.example.shoppinglist_sempraca.ui.chart.ChartViewModel
import com.example.shoppinglist_sempraca.ui.home.HomeViewModel
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationViewModel
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire app.
 */
object AppViewModelProvider {
    val factory = viewModelFactory {
        // Ini for HomeViewModel
        initializer {
            HomeViewModel(shopListApplication().container.repository)
        }
        //Ini for ItemManipulationViewModel
        initializer {
            ItemManipulationViewModel(shopListApplication().container.repository)
        }
        //Ini for ProductManipulationViewModel
        initializer {
            ProductManipulationViewModel(shopListApplication().container.repository)
        }
        initializer {
            ChartViewModel(shopListApplication().container.repository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ShopListApplication].
 */
fun CreationExtras.shopListApplication(): ShopListApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShopListApplication)