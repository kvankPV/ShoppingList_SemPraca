package com.example.shoppinglist_sempraca.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val repository: Repository) : ViewModel() {
    //retrieve all visible items from database
    val visibleItemsUiState: StateFlow<PagingData<Item>> =
        repository.getVisibleItemsStream().cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PagingData.empty()
            )

    //retrieve all non-visible items from database
    val nonVisibleItemsUiState: StateFlow<PagingData<Item>> =
        repository.getNonVisibleItemsStream().cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = PagingData.empty()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun getProductsFromItem(idItem: Int): Flow<PagingData<Product>> {
        return repository.getAllProductsFromItemStream(idItem)
    }
}