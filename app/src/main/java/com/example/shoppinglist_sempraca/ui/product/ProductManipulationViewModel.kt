package com.example.shoppinglist_sempraca.ui.product

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.data.Repository

class ProductManipulationViewModel(private val productsRepository: Repository) : ViewModel() {
    var productUiState by mutableStateOf(ProductUiState())
        private set

    fun updateUiState(productDetails: ProductDetails) {
        productUiState =
            ProductUiState(productDetails = productDetails, isEntryValid = validateInput(productDetails))
    }

    suspend fun insertProduct(itemId: Int) {
        if (validateInput()) {
            val product = productUiState.productDetails.copy(idItem = itemId).toProduct()
            productsRepository.insertProduct(product)
            productsRepository.updateItemVisibilityBasedOnProducts(itemId)
        }
    }

    suspend fun updateProduct(product: Product) {
        if (validateInput(product.toProductDetails())) {
            productsRepository.updateProduct(product)
            productsRepository.updateItemVisibilityBasedOnProducts(product.idItem)
        }
    }

    fun setCurrentProduct(product: Product) {
        productUiState = product.toProductUiState(isEntryValid = validateInput(product.toProductDetails()))
    }

    suspend fun deleteProduct(product: Product) {
        productsRepository.deleteProduct(product)
        productsRepository.updateItemVisibilityBasedOnProducts(product.idItem)
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
    idProduct = idProduct,
    idItem = idItem,
    name = name,
    category = category,
    quantity = quantity.toIntOrNull() ?: 0,
    price = price.toDoubleOrNull() ?: 0.0,
    checkedOut = checkedOut
)

fun Product.toProductUiState(isEntryValid: Boolean = false): ProductUiState = ProductUiState(
    productDetails = this.toProductDetails(),
    isEntryValid = isEntryValid
)

fun Product.toProductDetails(): ProductDetails = ProductDetails(
    idProduct = idProduct,
    idItem = idItem,
    name = name,
    category = category,
    quantity = quantity.toString(),
    price = price.toString(),
    checkedOut = checkedOut
)