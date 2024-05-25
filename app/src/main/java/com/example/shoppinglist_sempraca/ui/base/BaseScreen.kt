package com.example.shoppinglist_sempraca.ui.base

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationScreen
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationViewModel
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationScreen
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationViewModel
import com.example.shoppinglist_sempraca.ui.product.toProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseScreen {
    @Composable
    protected fun CardDropdownMenu(
        dropdownMenuExpanded: Boolean,
        onDismissRequest: () -> Unit,
        item: Item,
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        val itemManipulationViewModel: ItemManipulationViewModel =
            viewModel(factory = AppViewModelProvider.factory)
        var enabledEditing by rememberSaveable { mutableStateOf(false) }

        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.edit)) },
                onClick = {
                    itemManipulationViewModel.setCurrentItem(item)
                    enabledEditing = true
                    onDismissRequest()
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.delete)) },
                onClick = {
                    scope.launch {
                        itemManipulationViewModel.deleteItem(item)
                        onDismissRequest()
                    }
                }
            )
        }

        if (enabledEditing) {
            ItemManipulationScreen(
                viewModel = itemManipulationViewModel,
                itemUiState = itemManipulationViewModel.itemUiState,
                onItemValueChange = itemManipulationViewModel::updateUiState,
                onSaveClick = {
                    scope.launch {
                        itemManipulationViewModel.updateItem()
                        enabledEditing = false
                    }
                },
                onDismissRequest = { enabledEditing = false },
                isAddingNewItem = false
            )
        }
    }

    @Composable
    protected fun ListItemButton(
        expanded: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        IconButton(onClick = onClick, modifier = modifier) {
            Icon(imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(id = R.string.expand_button_content_desc),
                tint = MaterialTheme.colorScheme.secondary)
        }
    }

    @Composable
    protected fun PrintAllProducts(
        products: LazyPagingItems<Product>,
        isFromArchiveScreen: Boolean,
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        val productManipulationViewModel: ProductManipulationViewModel = viewModel(factory = AppViewModelProvider.factory)
        var enabledEditing by rememberSaveable { mutableStateOf(false) }

        Column(modifier = modifier) {
            products.itemSnapshotList.items.forEach { product ->
                ProductRow(
                    product = product,
                    productManipulationViewModel = productManipulationViewModel,
                    scope = scope,
                    onEditClick = {
                        productManipulationViewModel.setCurrentProduct(product)
                        enabledEditing = true
                    },
                    onDeleteClick = {
                        scope.launch { productManipulationViewModel.deleteProduct(product) }
                    }
                )
            }
        }

        if (enabledEditing) {
            ProductManipulationScreen(
                productUiState = productManipulationViewModel.productUiState,
                onProductValueChange = productManipulationViewModel::updateUiState,
                onSaveClick = {
                    scope.launch {
                        productManipulationViewModel.updateProduct(product = productManipulationViewModel.productUiState.productDetails.toProduct())
                        enabledEditing = false
                    }
                },
                onDismissRequest = { enabledEditing = false },
                isAddingNewProduct = false,
                isFromArchiveScreen = isFromArchiveScreen,
                viewModel = productManipulationViewModel
            )
        }
    }

    @Composable
    private fun ProductRow(
        product: Product,
        productManipulationViewModel: ProductManipulationViewModel,
        scope: CoroutineScope,
        onEditClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        var dropdownMenuExpanded by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RadioButton(
                selected = product.productCheckedOut,
                onClick = {
                    scope.launch {
                        val updatedProduct = product.copy(productCheckedOut = !product.productCheckedOut)
                        productManipulationViewModel.updateProduct(updatedProduct)
                    }
                }
            )
            val textState = remember(product) {
                derivedStateOf {
                    "${product.productName} ${product.productCategory} ${product.productQuantity} ks ${if (product.productPrice > 0.0) product.productPrice.toString() + " $" else ""}"
                }
            }
            Text(
                text = textState.value,
                style = MaterialTheme.typography.labelLarge,
                color = if (product.productCheckedOut) Color.Gray else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { dropdownMenuExpanded = true }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_options))
            }

            DropdownMenu(
                expanded = dropdownMenuExpanded,
                onDismissRequest = { dropdownMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.edit)) },
                    onClick = {
                        onEditClick()
                        dropdownMenuExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(id = R.string.delete)) },
                    onClick = {
                        onDeleteClick()
                        dropdownMenuExpanded = false
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.product_divider)))
    }
}