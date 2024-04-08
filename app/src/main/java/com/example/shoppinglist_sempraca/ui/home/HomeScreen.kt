package com.example.shoppinglist_sempraca.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    var showItemAddScreen by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
            onSaveClick = { scope.launch { itemManipulationViewModel.insertItem() } } ,
            onDismissRequest = { showItemAddScreen = false },
            isAddingNewItem = true
        )
    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>,
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (itemList.isEmpty()) {
            Text(text = stringResource(id = R.string.no_items_in_list),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
            )
        } else {
            ShopList(itemList = itemList,
                onItemClick = {item -> homeViewModel.getProductsFromItem(item.id)},
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
    //TODO If count of ticked of products = the size of list & both are not zero, then isVisible = false
    LazyColumn (modifier = modifier) {
        items (items = itemList.filter{it.isVisible}, key = {it.id}) {
                item -> ListCard (item = item,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable { onItemClick(item) },
                    products = homeViewModel.getProductsFromItem(item.id).collectAsState(initial= emptyList()).value
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
    var expanded by remember {
        mutableStateOf(false)
    }
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.combinedClickable(
            onClick = {expanded = !expanded},
            onLongClick = {dropdownMenuExpanded = true}),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = item.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                val numberOfCheckedOut = products.count { it.checkedOut }
                Text(
                    text = numberOfCheckedOut.toString() + " / " + products.size.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                ListItemButton(expanded = expanded, onClick = { expanded = !expanded })

                if (expanded) {
                    PrintAllProducts(products = products,
                        modifier = Modifier.padding(
                            start = dimensionResource(R.dimen.padding_medium),
                            top = dimensionResource(R.dimen.padding_small),
                            end = dimensionResource(R.dimen.padding_medium),
                            bottom = dimensionResource(R.dimen.padding_medium)
                        ))
                }
            }
        }

        val scope = rememberCoroutineScope()
        var enabledEditing by remember {
            mutableStateOf(false)
        }
        val itemManipulationViewModel: ItemManipulationViewModel = viewModel(factory = AppViewModelProvider.factory)

        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { dropdownMenuExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.edit)) },
                onClick = { itemManipulationViewModel.setCurrentItem(item)
                    enabledEditing = true
                dropdownMenuExpanded = false} )
            DropdownMenuItem(
                text = { Text(stringResource(id = R.string.delete))},
                onClick = { scope.launch { itemManipulationViewModel.deleteItem(item)
                dropdownMenuExpanded = false}} )
        }
        if (enabledEditing) {
            ItemManipulationScreen(
                itemUiState = itemManipulationViewModel.itemUiState,
                onItemValueChange = itemManipulationViewModel::updateUiState,
                onSaveClick = { scope.launch {
                        itemManipulationViewModel.updateItem()
                        enabledEditing = false
                    } },
                onDismissRequest = { enabledEditing = false },
                isAddingNewItem = false
            )
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
        Icon(imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
            contentDescription = stringResource(id = R.string.expand_button_content_desc),
            tint = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun PrintAllProducts(
    products: List<Product>, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row {
            products.forEach { product ->
                Text(
                    text = "${product.name} ${product.category} ${product.quantity} ${if (product.price > 0.0) product.price.toString() else ""}",
                    style = MaterialTheme.typography.labelSmall
                )
                //TODO have a dropdown menu with the option of Edit and Delete | Icons.Filled.MoreVert
            }
        }
    }
}