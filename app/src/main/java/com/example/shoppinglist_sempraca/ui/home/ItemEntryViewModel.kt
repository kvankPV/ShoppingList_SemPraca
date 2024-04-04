package com.example.shoppinglist_sempraca.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.ItemsRepository

//insert items and validation
class ItemEntryViewModel (private val itemsRepository: ItemsRepository) : ViewModel() {

    /**
     * Holds current item ui state
     */
    var itemUiState by mutableStateOf(ItemUiState())
    private set

    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState =
            ItemUiState(itemDetails = itemDetails, isEntryValid = validateInput(itemDetails))
    }

    /**
     * Inserts an [Item] in the Room database
     */
    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() || isVisible.isNotBlank() || totalPrice.isNotBlank()
        }
    }
}

data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val name: String = "",
    val isVisible: String = "",
    val totalPrice: String = ""
)

fun ItemDetails.toItem(): Item = Item (
    name = name,
    isVisible = isVisible.toBooleanStrictOrNull() ?: true,
    totalPrice = totalPrice.toDoubleOrNull() ?: 0.0
)

fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

fun Item.toItemDetails(): ItemDetails = ItemDetails(
    name = name,
    isVisible = isVisible.toString(),
    totalPrice = totalPrice.toString(),
)