package com.example.shoppinglist_sempraca.ui.product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.data.Repository
import com.example.shoppinglist_sempraca.ui.base.BaseViewModel
import kotlinx.coroutines.flow.first

class ProductManipulationViewModel(private val productsRepository: Repository) : BaseViewModel() {
    var productUiState by mutableStateOf(ProductUiState())
        private set

    fun updateUiState(productDetails: ProductDetails) {
        productUiState =
            ProductUiState(productDetails = productDetails, isEntryValid = validateInput(productDetails))
    }

    suspend fun insertProduct(itemId: Int) {
        if (validateInput()) {
            val product = productUiState.productDetails.copy(idItem = itemId).toProduct()
            productsRepository.insertStream(product)
        }
    }

    suspend fun updateProduct(product: Product) {
        if (validateInput(product.toProductDetails())) {
            productsRepository.updateStream(product)
            val checkedOutAmount = productsRepository.countAllCheckedOutProductsFromItemStream(product.itemId)
            val totalAmount = productsRepository.countAllProductsFromItemStream(product.itemId)
            if (checkedOutAmount == totalAmount) {
                val item = productsRepository.getItemStream(product.itemId).first()
                if (item != null) {
                    val updatedItem = item.copy(itemVisibility = false)
                    productsRepository.updateStream(updatedItem)
                }
            }
        }
    }

    fun setCurrentProduct(product: Product) {
        productUiState = product.toProductUiState(isEntryValid = validateInput(product.toProductDetails()))
    }

    suspend fun deleteProduct(product: Product) {
        productsRepository.deleteStream(product)
    }

    private fun validateInput(uiState: ProductDetails = productUiState.productDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && category.isNotBlank() && quantity.isNotBlank()
        }
    }
}

data class ProductUiState(
    val productDetails: ProductDetails = ProductDetails(),
    val isEntryValid: Boolean = false
)
data class ProductDetails(
    val idProduct: Int = 0,
    val idItem: Int = 0,
    val name: String = "",
    val category: String = "",
    val quantity: String = "",
    val price: String = "",
    val checkedOut: Boolean = false
)

fun ProductDetails.toProduct(): Product = Product (
    productId = idProduct,
    itemId = idItem,
    productName = name,
    productCategory = category,
    productQuantity = quantity.toIntOrNull() ?: 0,
    productPrice = price.toDoubleOrNull() ?: 0.0,
    productCheckedOut = checkedOut
)

fun Product.toProductUiState(isEntryValid: Boolean = false): ProductUiState = ProductUiState(
    productDetails = this.toProductDetails(),
    isEntryValid = isEntryValid
)

fun Product.toProductDetails(): ProductDetails = ProductDetails(
    idProduct = productId,
    idItem = itemId,
    name = productName,
    category = productCategory,
    quantity = productQuantity.toString(),
    price = productPrice.toString(),
    checkedOut = productCheckedOut
)