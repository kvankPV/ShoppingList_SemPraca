package com.example.shoppinglist_sempraca.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ShoppingListTopBar
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.home.HomeDestination.titleRes
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationScreen
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationViewModel
import com.example.shoppinglist_sempraca.ui.navigation.NavigationDestination
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationScreen
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationViewModel
import com.example.shoppinglist_sempraca.ui.product.toProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}
/*
Important behavior for coroutineScopes:
If the user rotates the screen very fast, the operation may get cancelled
and the item may not be saved/updated in the Database. This is because when config
change occurs, the Activity will be recreated and the rememberCoroutineScope will
be cancelled - since the scope is bound to composition.
*/

//Inspired by this source:
//https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ModalBottomSheet(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.material3.SheetState,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Shape,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,kotlin.Function0,androidx.compose.foundation.layout.WindowInsets,androidx.compose.material3.ModalBottomSheetProperties,kotlin.Function1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var showItemAddScreen by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(bottom = dimensionResource(id = R.dimen.topBar_divider)),
        topBar = {
            ShoppingListTopBar(
                title = stringResource(titleRes),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {showItemAddScreen = true},
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_item)
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            itemList = homeUiState.itemList,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            homeViewModel = homeViewModel
        )
    }
    if (showItemAddScreen) {
        val itemManipulationViewModel: ItemManipulationViewModel = viewModel(factory = AppViewModelProvider.factory)
        ItemManipulationScreen(
            itemUiState = itemManipulationViewModel.itemUiState,
            onItemValueChange = itemManipulationViewModel::updateUiState,
            onSaveClick = { scope.launch { itemManipulationViewModel.insertItem() } },
            onDismissRequest = { showItemAddScreen = false },
            isAddingNewItem = true,
            viewModel = itemManipulationViewModel
        )
    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel
) {
    val visibleItems = remember(itemList) {
        derivedStateOf {
            itemList.filter {
                it.itemVisibility
            }
        }
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (visibleItems.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_items_in_list),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            ShopList(itemList = visibleItems.value,
                onItemClick = {item -> homeViewModel.getProductsFromItem(item.itemId)},
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                homeViewModel = homeViewModel
                )
        }
    }
}

@Composable
private fun ShopList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn (modifier = modifier) {
        items (items = itemList, key = {it.itemId}) {
                item -> ListCard (item = item,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable { onItemClick(item) },
                    products = homeViewModel.getProductsFromItem(item.itemId).collectAsState(initial= emptyList()).value
                )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListCard(
    item: Item,
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false)}
    val (dropdownMenuExpanded, onDropdownMenuExpandedChange) = rememberSaveable { mutableStateOf(false)}

    Card(
        modifier = modifier.combinedClickable(
            onClick = { onExpandedChange(!expanded) },
            onLongClick = { onDropdownMenuExpandedChange(true) }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.padding_small))
    ) {
        CardContent(item, products, expanded, onExpandedChange)
        CardDropdownMenu(
            dropdownMenuExpanded = dropdownMenuExpanded,
            onDismissRequest = { onDropdownMenuExpandedChange(false) },
            item = item
        )
        if (expanded) {
            ExpandedCardContent(products, item)
        }
    }
}

@Composable
private fun CardContent(
    item: Item,
    products: List<Product>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = item.itemName, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
            val numberOfCheckedOut = remember (products) {
                derivedStateOf {
                    products.count {it.productCheckedOut}
                }
            }
            Text(
                text = "${numberOfCheckedOut.value} / ${products.size}",
                style = MaterialTheme.typography.titleMedium
            )
            ListItemButton(expanded = expanded, onClick = { onExpandedChange(!expanded) })
        }
    }
}

@Composable
private fun CardDropdownMenu(
    dropdownMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    item: Item,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val itemManipulationViewModel: ItemManipulationViewModel = viewModel(factory = AppViewModelProvider.factory)
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
private fun ExpandedCardContent(
    products: List<Product>,
    item: Item,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val productManipulationViewModel: ProductManipulationViewModel =
        viewModel(factory = AppViewModelProvider.factory)
    var addProduct by rememberSaveable { mutableStateOf(false) }

    if (products.isNotEmpty()) {
        PrintAllProducts(
            products = products,
            modifier = modifier.padding(
                start = dimensionResource(R.dimen.padding_medium),
                top = dimensionResource(R.dimen.padding_small),
                end = dimensionResource(R.dimen.padding_medium),
                bottom = dimensionResource(R.dimen.padding_medium),

            )
        )
    }

    AddProductCard(onClick = { addProduct = true })
    if (addProduct) {
        ProductManipulationScreen(
            productUiState = productManipulationViewModel.productUiState,
            onProductValueChange = productManipulationViewModel::updateUiState,
            onSaveClick = { scope.launch { productManipulationViewModel.insertProduct(item.itemId) } },
            onDismissRequest = { addProduct = false },
            isAddingNewProduct = true,
            isFromArchiveScreen = false,
            viewModel = productManipulationViewModel
        )
    }
}

@Composable
private fun AddProductCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.padding_small))
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Text(text = stringResource(id = R.string.add_product), style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)))
            Icon(imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_product))
        }
    }
}

@Composable
private fun ListItemButton(
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
private fun PrintAllProducts(
    products: List<Product>,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val productManipulationViewModel: ProductManipulationViewModel = viewModel(factory = AppViewModelProvider.factory)
    var enabledEditing by rememberSaveable { mutableStateOf(false) }

    val sortedProducts = remember(products) {
        derivedStateOf {
            products.sortedWith(compareBy({ !it.productCheckedOut }, { it.productName }))
        }
    }

    Column(modifier = modifier) {
        sortedProducts.value.forEach { product ->
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
            isFromArchiveScreen = false,
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
                "${product.productName} ${product.productCategory} ${product.productQuantity} ${if (product.productPrice > 0.0) product.productPrice.toString() else ""}"
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