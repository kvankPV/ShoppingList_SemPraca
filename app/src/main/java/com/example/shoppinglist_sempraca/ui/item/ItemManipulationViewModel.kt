package com.example.shoppinglist_sempraca.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Repository

//Insert, update, delete items. with validation?
class ItemManipulationViewModel(private val repository: Repository) : ViewModel() {
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

    suspend fun insertItem() {
        if (validateInput()) {
            repository.insertItem(itemUiState.itemDetails.toItem())
        }
    }

    /**
     * Sets the [itemUiState] to the [item] that the user wants to manipulate.
     */
    fun setCurrentItem(item: Item) {
        itemUiState = item.toItemUiState(isEntryValid = validateInput(item.toItemDetails()))
    }

    suspend fun updateItem() {
        if (validateInput()) {
            repository.updateItem(itemUiState.itemDetails.toItem())
        }
    }

    suspend fun deleteItem(item: Item) {
        repository.deleteItem(item)
    }

    private fun validateInput(uiState: ItemDetails = itemUiState.itemDetails): Boolean {
        return with(uiState) {
            name.isNotBlank()
        }
    }
}
/**
 * Represents Ui State for an Item.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val isVisible: String = "",
    val totalPrice: String = ""
)

/**
 * Extension function to convert [ItemUiState] to [Item].
 */
fun ItemDetails.toItem(): Item = Item (
    id = id,
    name = name,
    isVisible = isVisible.toBooleanStrictOrNull() ?: true,
    totalPrice = totalPrice.toDoubleOrNull() ?: 0.0
)

/**
 * Extension function to convert [Item] to [ItemUiState].
 */
fun Item.toItemUiState(isEntryValid: Boolean = false): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails].
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    totalPrice = totalPrice.toString(),
    isVisible = isVisible.toString()
)