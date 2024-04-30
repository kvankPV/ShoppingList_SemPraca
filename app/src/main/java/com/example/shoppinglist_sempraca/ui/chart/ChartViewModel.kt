package com.example.shoppinglist_sempraca.ui.chart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.shoppinglist_sempraca.data.Repository
import kotlinx.coroutines.flow.first
import kotlin.math.round

class ChartViewModel(private val repository: Repository) : ViewModel() {
    private val invisibleItems = mutableStateOf(listOf<Double>())
    private val cumulativeAverages = mutableStateOf(listOf<Double>())

    private fun getCumulativeAverages(): List<Double> {
        val cumulativeAverages = mutableListOf<Double>()
        var sum = 0.0
        for ((index, value) in invisibleItems.value.withIndex()) {
            sum += value
            val average = sum / (index + 1)
            cumulativeAverages.add(average)
        }
        return cumulativeAverages
    }

    suspend fun fetchInvisibleItems() {
        val items = repository.getAllPricesFromNonVisibleItems().first()
        invisibleItems.value = items
        cumulativeAverages.value = getCumulativeAverages()
    }

    fun averageTotalPrice(): Double {
        val average = invisibleItems.value.average()
        return round(average * 100) / 100
    }

    fun getFloatItems(): List<Float> {
        return invisibleItems.value.map { it.toFloat() }
    }

    fun hasInvisibleItems(): Boolean {
        return invisibleItems.value.isNotEmpty()
    }

    fun getCumulativeAveragesAsFloat(): List<Float> {
        return cumulativeAverages.value.map { it.toFloat() }
    }
}