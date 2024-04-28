package com.example.shoppinglist_sempraca.ui.product

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.shoppinglist_sempraca.R

@Composable
fun ProductManipulationScreen(
    productUiState: ProductUiState,
    onProductValueChange: (ProductDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    isAddingNewProduct: Boolean,
    isFromArchiveScreen: Boolean
) {
    val (openBottomSheet, resetOpenBottomSheet) = rememberOpenBottomSheetState()
    val (textFieldValue, onTextFieldValueChange, resetTextFieldValue) = rememberTextFieldValue(
        isAddingNewProduct,
        productUiState.productDetails.name
    )
    val (selectedCategory, onSelectedCategoryChange, resetSelectedCategory) = rememberSelectedCategory(
        productUiState.productDetails.category,
        isAddingNewProduct
    )
    val (productQuantity, onProductQuantityChange, resetProductQuantity) = rememberProductQuantity(
        productUiState.productDetails.quantity,
        isAddingNewProduct
    )

    if (openBottomSheet) {
        ProductBottomSheet(
            textFieldValue = textFieldValue,
            onTextFieldValueChange = onTextFieldValueChange,
            selectedCategory = selectedCategory,
            onSelectedCategoryChange = onSelectedCategoryChange,
            productQuantity = productQuantity,
            onProductQuantityChange = onProductQuantityChange,
            onProductValueChange = onProductValueChange,
            productUiState = productUiState,
            isFromArchiveScreen = isFromArchiveScreen,
            onSaveClick = {
                onSaveClick()
                resetOpenBottomSheet()
                resetTextFieldValue()
                resetSelectedCategory()
                resetProductQuantity()
            }
        ) {
            onDismissRequest()
            resetTextFieldValue()
            resetSelectedCategory()
            resetProductQuantity()
        }
    }
}

@Composable
private fun rememberOpenBottomSheetState(): Pair<Boolean, () -> Unit> {
    val (openBottomSheet, setOpenBottomSheet) = rememberSaveable { mutableStateOf(true) }
    val resetOpenBottomSheet: () -> Unit = { setOpenBottomSheet(false) }
    return openBottomSheet to resetOpenBottomSheet
}

@Composable
private fun rememberTextFieldValue(
    isAddingNewProduct: Boolean,
    initialValue: String
): Triple<String, (String) -> Unit, () -> Unit> {
    val (textFieldValue, setTextFieldValue) = rememberSaveable {
        mutableStateOf(if (isAddingNewProduct) "" else initialValue)
    }
    val onTextFieldValueChange: (String) -> Unit = { setTextFieldValue(it) }
    val resetTextFieldValue: () -> Unit = { setTextFieldValue("") }
    return Triple(textFieldValue, onTextFieldValueChange, resetTextFieldValue)
}

@Composable
private fun rememberSelectedCategory(
    initialCategory: String,
    isAddingNewProduct: Boolean
): Triple<String, (String) -> Unit, () -> Unit> {
    val (selectedCategory, setSelectedCategory) = rememberSaveable {
        mutableStateOf(if (isAddingNewProduct) "" else initialCategory)
    }
    val onSelectedCategoryChange: (String) -> Unit = { setSelectedCategory(it) }
    val resetSelectedCategory: () -> Unit = { setSelectedCategory("") }
    return Triple(selectedCategory, onSelectedCategoryChange, resetSelectedCategory)
}

@Composable
private fun rememberProductQuantity(
    initialQuantity: String,
    isAddingNewProduct: Boolean
): Triple<String, (String) -> Unit, () -> Unit> {
    val (productQuantity, setProductQuantity) = rememberSaveable {
        mutableStateOf(if (isAddingNewProduct) "" else initialQuantity)
    }
    val onProductQuantityChange: (String) -> Unit = { setProductQuantity(it) }
    val resetProductQuantity: () -> Unit = { setProductQuantity("") }
    return Triple(productQuantity, onProductQuantityChange, resetProductQuantity)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductBottomSheet(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    selectedCategory: String,
    onSelectedCategoryChange: (String) -> Unit,
    productQuantity: String,
    onProductQuantityChange: (String) -> Unit,
    onProductValueChange: (ProductDetails) -> Unit,
    productUiState: ProductUiState,
    isFromArchiveScreen: Boolean,
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)
    val windowInsets = if (edgeToEdgeEnabled) WindowInsets(0) else BottomSheetDefaults.windowInsets

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = bottomSheetState,
        windowInsets = windowInsets
    ) {
        Column {
            ProductNameTextField(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = onTextFieldValueChange,
                onProductValueChange = onProductValueChange,
                productUiState = productUiState
            )

            ProductCategoryDropdown(
                selectedCategory = selectedCategory,
                onSelectedCategoryChange = onSelectedCategoryChange,
                onProductValueChange = onProductValueChange,
                productUiState = productUiState
            )

            ProductQuantityField(
                productQuantity = productQuantity,
                onProductQuantityChange = onProductQuantityChange,
                onProductValueChange = onProductValueChange,
                productUiState = productUiState
            )

            if (isFromArchiveScreen) {
                ProductPriceField(
                    productPrice = productUiState.productDetails.price,
                    onProductPriceChange = { newPrice ->
                        onProductValueChange(productUiState.productDetails.copy(price = newPrice))
                    }
                )
            }

            SaveButton(
                onSaveClick = onSaveClick,
                onDismissRequest = onDismissRequest,
                textFieldValue = textFieldValue,
                selectedCategory = selectedCategory,
                productQuantity = productQuantity
            )
        }
    }
}

@Composable
private fun ProductNameTextField(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onProductValueChange: (ProductDetails) -> Unit,
    productUiState: ProductUiState,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        value = textFieldValue,
        onValueChange = {
            onTextFieldValueChange(it)
            onProductValueChange(productUiState.productDetails.copy(name = it))
        },
        singleLine = true,
        label = { Text(text = stringResource(id = R.string.entry_name)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductCategoryDropdown(
    selectedCategory: String,
    onSelectedCategoryChange: (String) -> Unit,
    onProductValueChange: (ProductDetails) -> Unit,
    productUiState: ProductUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories = context.resources.getStringArray(R.array.categories).toList()
    val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = allowExpanded,
        onExpandedChange = setExpanded,
        modifier = modifier
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            value = selectedCategory,
            onValueChange = onSelectedCategoryChange,
            singleLine = true,
            label = { Text(text = stringResource(id = R.string.category)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = allowExpanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            readOnly = true
        )
        ExposedDropdownMenu(
            expanded = allowExpanded,
            onDismissRequest = { setExpanded(false) },
            modifier = Modifier.focusable(false)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        onSelectedCategoryChange(category)
                        onProductValueChange(productUiState.productDetails.copy(category = category))
                        setExpanded(false)
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
@Composable
private fun ProductQuantityField(
    productQuantity: String,
    onProductQuantityChange: (String) -> Unit,
    productUiState: ProductUiState,
    modifier: Modifier = Modifier,
    onProductValueChange: (ProductDetails) -> Unit
) {
    OutlinedTextField(
        value = productQuantity,
        onValueChange = { newValue ->
            onProductQuantityChange(newValue)
            onProductValueChange(productUiState.productDetails.copy(quantity = newValue))
        },
        singleLine = true,
        label = { Text(stringResource(id = R.string.product_quantity_label)) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        leadingIcon = {
            IconButton(
                onClick = {
                    val currentQuantity = productQuantity.toIntOrNull() ?: 0
                    val newQuantity = (currentQuantity - 1).coerceAtLeast(0)
                    val newQuantityString = newQuantity.toString()
                    onProductQuantityChange(newQuantityString)
                    onProductValueChange(productUiState.productDetails.copy(quantity = newQuantityString))
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_minus),
                    contentDescription = stringResource(id = R.string.decrement_quantity)
                )
            }
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    val currentQuantity = productQuantity.toIntOrNull() ?: 0
                    val newQuantity = currentQuantity + 1
                    val newQuantityString = newQuantity.toString()
                    onProductQuantityChange(newQuantityString)
                    onProductValueChange(productUiState.productDetails.copy(quantity = newQuantityString))
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.increment_quantity)
                )
            }
        }
    )
}

@Composable
private fun ProductPriceField(
    productPrice: String,
    onProductPriceChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = productPrice,
        onValueChange = onProductPriceChange,
        singleLine = true,
        label = { Text(stringResource(id = R.string.product_price_label)) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun SaveButton(
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    textFieldValue: String,
    selectedCategory: String,
    productQuantity: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onSaveClick()
            onDismissRequest()
        },
        modifier = modifier,
        enabled = textFieldValue.isNotEmpty() && selectedCategory.isNotEmpty() && productQuantity.isNotEmpty()
    ) {
        Text(text = stringResource(id = R.string.submit))
    }
}