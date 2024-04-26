package com.example.shoppinglist_sempraca.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: Repository) : ViewModel() {
    //retrieve all items from database
    val homeUiState: StateFlow<HomeUiState> =
        repository.getAllItemsStream().map { HomeUiState(it) }
            .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun getProductsFromItem(idItem: Int): Flow<List<Product>> {
        return repository.getAllProductsFromItemStream(idItem)
    }
}

data class HomeUiState(val itemList: List<Item> = listOf())