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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.shoppinglist_sempraca.ui.home.ArchiveDestination.titleRes
import com.example.shoppinglist_sempraca.ui.item.ItemManipulationViewModel
import com.example.shoppinglist_sempraca.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object ArchiveDestination : NavigationDestination {
    override val route = "archive"
    override val titleRes = R.string.archive
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
class ArchiveScreen (
    private val modifier: Modifier = Modifier
): BaseScreen() {
    @SuppressLint("NotConstructor")
    @Composable
    fun ArchiveScreen(
    ) {
        val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
        ) { innerPadding ->
            ArchiveBody(
                homeUiState = homeViewModel.nonVisibleItemsUiState.collectAsLazyPagingItems().itemSnapshotList.items,
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                homeViewModel = homeViewModel
            )
        }
    }

    @Composable
    private fun ArchiveBody(
        homeUiState: List<Item>,
        modifier: Modifier = Modifier,
        homeViewModel: HomeViewModel
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            if (homeUiState.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_archive),
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
        itemList: List<Item>,
        onItemClick: (Item) -> Unit,
        homeViewModel: HomeViewModel,
        modifier: Modifier = Modifier
    ) {
        LazyColumn (modifier = modifier) {
            items(count = itemList.size) { index ->
                    val products = homeViewModel.getProductsFromItem(itemList[index].itemId).collectAsLazyPagingItems()
                    ListCard (
                        item = itemList[index],
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.padding_small))
                            .clickable { onItemClick(itemList[index]) },
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
        modifier: Modifier = Modifier,
    ) {
        val (expanded, onExpandedChange) = rememberSaveable { mutableStateOf(false) }
        val (dropdownMenuExpanded, onDropdownMenuExpandedChange) = rememberSaveable {
            mutableStateOf(false)
        }

        Card(
            modifier = modifier.combinedClickable(
                onClick = { onExpandedChange(!expanded) },
                onLongClick = { onDropdownMenuExpandedChange(true) }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.padding_small))
        ) {
            val productList = products.itemSnapshotList.items
            val checkedOutCount = productList.count { it.productCheckedOut }
            val totalCount = productList.size
            val totalCost = productList.sumOf { it.productPrice }

            CardContent(
                item,
                checkedOutCount,
                totalCount,
                totalCost,
                expanded,
                onExpandedChange
            )
            CardDropdownMenu(
                dropdownMenuExpanded = dropdownMenuExpanded,
                onDismissRequest = { onDropdownMenuExpandedChange(false) },
                item = item
            )
            if (expanded) {
                ExpandedCardContent(productList)
            }
        }
    }

    @Composable
    private fun CardContent(
        item: Item,
        numberOfCheckedOut: Int,
        totalProducts: Int,
        totalPrice: Double,
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val scope = rememberCoroutineScope()
        val itemManipulationViewModel: ItemManipulationViewModel =
            viewModel(factory = AppViewModelProvider.factory)
        Column(
            modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.Gray)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$numberOfCheckedOut / $totalProducts",
                    style = MaterialTheme.typography.titleMedium
                )
                ListItemButton(expanded = expanded, onClick = { onExpandedChange(!expanded) })
            }

            if (totalPrice > 0.0 && item.itemTotalPrice != totalPrice) {
                LaunchedEffect(key1 = totalPrice) {
                    scope.launch {
                        itemManipulationViewModel.updateItemTotalPrice(item.itemId, totalPrice)
                    }
                }
            }

            Text(
                text = "Total price: ${item.itemTotalPrice} $",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    @Composable
    private fun ExpandedCardContent(
        products: List<Product>,
        modifier: Modifier = Modifier
    ) {
        if (products.isNotEmpty()) {
            PrintAllProducts(
                products = products,
                modifier = modifier.padding(
                    start = dimensionResource(R.dimen.padding_medium),
                    top = dimensionResource(R.dimen.padding_small),
                    end = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_medium),
                    ),
                isFromArchiveScreen = true
            )
        }
    }
}