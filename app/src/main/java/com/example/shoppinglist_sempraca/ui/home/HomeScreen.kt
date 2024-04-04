package com.example.shoppinglist_sempraca.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ShoppingListTopBar
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.home.HomeDestination.titleRes
import com.example.shoppinglist_sempraca.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

//Inšpirované z tohto zdroja:
//https://developer.android.com/reference/kotlin/androidx/compose/material3/package-summary#ModalBottomSheet(kotlin.Function0,androidx.compose.ui.Modifier,androidx.compose.material3.SheetState,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Shape,androidx.compose.ui.graphics.Color,androidx.compose.ui.graphics.Color,androidx.compose.ui.unit.Dp,androidx.compose.ui.graphics.Color,kotlin.Function0,androidx.compose.foundation.layout.WindowInsets,androidx.compose.material3.ModalBottomSheetProperties,kotlin.Function1)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory),
    itemEntryViewModel: ItemEntryViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShoppingListTopBar(
                title = stringResource(titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openBottomSheet = true },
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
            viewmodel = homeViewModel
        )
        if (openBottomSheet) {
            val windowInsets = if (edgeToEdgeEnabled)
                WindowInsets(0) else BottomSheetDefaults.windowInsets

            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState,
                windowInsets = windowInsets
            ) {

                val isValid: Boolean = itemEntryViewModel.itemUiState.isEntryValid
                val itemDetails: ItemDetails = itemEntryViewModel.itemUiState.itemDetails
                val onItemValueChange: (ItemDetails) -> Unit = itemEntryViewModel::updateUiState
                Column {
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        value = itemDetails.name,
                        onValueChange = {onItemValueChange(itemDetails.copy(name = it))},
                        singleLine = true,
                        label = { Text( text = stringResource(id = R.string.entry_name)
                    )})
                    Button(onClick = { scope.launch { itemEntryViewModel.saveItem() }
                        openBottomSheet = false},
                        enabled = isValid) {
                        Text(text = stringResource(id = R.string.submit))
                    }
                }

            }
        }
    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>, modifier: Modifier = Modifier, viewmodel: HomeViewModel
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
                onItemClick = {item ->
                              viewmodel.getProductsFromItem(item.name)},
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small)),
                viewmodel = viewmodel
                )
        }
    }
}

@Composable
private fun ShopList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    viewmodel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    //TODO If count of ticked of products = the size of list & both are not zero, then isVisible = false
    LazyColumn (modifier = modifier) {
        items (items = itemList.filter{it.isVisible}, key = {it.name}) {
                item -> ListCard (item = item,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable { onItemClick(item) },
                    products = viewmodel.getProductsFromItem(item.name).collectAsState(initial= emptyList()).value
                )
        }
    }
}

@Composable
private fun ListCard(
    item: Item, products: List<Product>, modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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