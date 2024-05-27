package com.example.shoppinglist_sempraca.ui.home

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ShoppingListTopBar
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.base.BaseScreen
import com.example.shoppinglist_sempraca.ui.home.HomeDestination.titleRes
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationScreen
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationViewModel
import com.example.shoppinglist_sempraca.ui.navigation.NavigationDestination
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationScreen
import com.example.shoppinglist_sempraca.ui.product.ProductManipulationViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
class HomeScreen (
    private val modifier: Modifier = Modifier
): BaseScreen() {
    @SuppressLint("NotConstructor")
    @Composable
    fun HomeScreen() {
        val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
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
                    onClick = { showItemAddScreen = true },
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
                homeUiState = homeViewModel.visibleItemsUiState.collectAsLazyPagingItems(),
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                homeViewModel = homeViewModel
            )
        }
        if (showItemAddScreen) {
            val itemManipulationViewModel: ItemManipulationViewModel =
                viewModel(factory = AppViewModelProvider.factory)
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
        homeUiState: LazyPagingItems<Item>,
        modifier: Modifier = Modifier,
        homeViewModel: HomeViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            if (homeUiState.itemCount == 0) {
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
                ShopList(
                    itemList = homeUiState,
                    onItemClick = { item -> homeViewModel.getProductsFromItem(item.itemId) },
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                    homeViewModel = homeViewModel
                )
            }
        }
    }

    @Composable
    private fun ShopList(
        itemList: LazyPagingItems<Item>,
        onItemClick: (Item) -> Unit,
        homeViewModel: HomeViewModel,
        modifier: Modifier = Modifier
    ) {
        LazyColumn (modifier = modifier) {
            items (itemList.itemSnapshotList.items) { item ->
                val products = homeViewModel.getProductsFromItem(item.itemId).collectAsLazyPagingItems()
                ListCard (
                    item = item,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable { onItemClick(item) },
                    products = products
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ListCard(
        item: Item,
        products: LazyPagingItems<Product>,
        modifier: Modifier = Modifier
    ) {
        val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
        val (dropdownMenuExpanded, onDropdownMenuExpandedChange) = rememberSaveable { mutableStateOf(false) }

        Card(
            modifier = modifier.combinedClickable(
                onClick = { onExpandedChange(!expanded) },
                onLongClick = { onDropdownMenuExpandedChange(true) }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.padding_small))
        ) {
            val (numberOfCheckedOut, totalProducts) = remember(products.itemSnapshotList.items) {
                val items = products.itemSnapshotList.items
                val checkedOutCount = items.count { it.productCheckedOut }
                val totalCount = items.size
                Pair(checkedOutCount, totalCount)
            }
            CardContent(item, numberOfCheckedOut, totalProducts, expanded, onExpandedChange)
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
        numberOfCheckedOut: Int,
        totalProducts: Int,
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
                Text(
                    text = "$numberOfCheckedOut / $totalProducts",
                    style = MaterialTheme.typography.titleMedium
                )
                ListItemButton(expanded = expanded, onClick = { onExpandedChange(!expanded) })
            }
        }
    }

    @Composable
    private fun ExpandedCardContent(
        products: LazyPagingItems<Product>,
        item: Item,
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        val productManipulationViewModel: ProductManipulationViewModel =
            viewModel(factory = AppViewModelProvider.factory)
        var addProduct by rememberSaveable { mutableStateOf(false) }

        if (products.itemCount > 0) {
            PrintAllProducts(
                products = products,
                modifier = modifier.padding(
                    start = dimensionResource(R.dimen.padding_medium),
                    top = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_medium),
                    ),
                isFromArchiveScreen = false
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
                Text(
                    text = stringResource(id = R.string.add_product),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_product)
                )
            }
        }
    }
}