package com.example.shoppinglist_sempraca.ui.chart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppinglist_sempraca.data.Repository
import kotlinx.coroutines.flow.first
import kotlin.math.round

class ChartViewModel(private val repository: Repository) : ViewModel() {
    private val invisibleItems = mutableStateOf(listOf<Double>())

    suspend fun fetchInvisibleItems() {
        val items = repository.getAllPricesFromNonVisibleItems().first()
        invisibleItems.value = items
    }

    fun averageTotalPrice(): Double {
        val average = invisibleItems.value.average()
        return round(average * 100) / 100
    }

    fun getItemPrice(index: Int): Double? {
        return invisibleItems.value.getOrNull(index)
    }
}