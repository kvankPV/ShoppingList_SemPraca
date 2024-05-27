@file:Suppress("EmptyMethod", "EmptyMethod")

package com.example.shoppinglist_sempraca.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Repository
import com.example.shoppinglist_sempraca.ui.base.BaseViewModel
import kotlinx.coroutines.flow.first

//Insert, update, delete items. with validation?
class ItemManipulationViewModel(private val repository: Repository) : BaseViewModel() {
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
            repository.insertStream(itemUiState.itemDetails.toItem())
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
            repository.updateStream(itemUiState.itemDetails.toItem())
        }
    }

    suspend fun deleteItem(item: Item) {
        repository.deleteStream(item)
    }

    suspend fun updateItemTotalPrice(itemId: Int, totalPrice: Double) {
        val item = repository.getItemStream(itemId)
        val updatedItem = item.first()?.copy(itemTotalPrice = totalPrice)
        repository.updateStream(updatedItem)
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
    itemId = id,
    itemName = name,
    itemVisibility = isVisible.toBooleanStrictOrNull() ?: true,
    itemTotalPrice = totalPrice.toDoubleOrNull() ?: 0.0
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
    id = itemId,
    name = itemName,
    totalPrice = itemTotalPrice.toString(),
    isVisible = itemVisibility.toString()
)