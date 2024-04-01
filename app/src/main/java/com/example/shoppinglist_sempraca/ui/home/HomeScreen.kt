package com.example.shoppinglist_sempraca.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ShoppingListTopBar
import com.example.shoppinglist_sempraca.data.Item
import com.example.shoppinglist_sempraca.data.Product
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.theme.ShoppingList_SemPracaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    modifier: Modifier = Modifier,
    navigateToItemEntry: () -> Unit,
    navigateToItemUpdate: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold (
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ShoppingListTopBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                addMenu = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToItemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            ) {
               Icon(imageVector = Icons.Default.Add,
                   contentDescription = stringResource(id = R.string.add_item))
            }
        },
    ) {
        innerPadding ->
        HomeBody(itemList = homeUiState.itemList, onItemClick = navigateToItemUpdate,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize())

    }
}

@Composable
private fun HomeBody(
    itemList: List<Item>, onItemClick: (String) -> Unit, modifier: Modifier = Modifier
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (itemList.isEmpty()) {
            Text(text = stringResource(id = R.string.no_items_in_list),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        } else {
            ShopList(itemList = itemList,
                onItemClick = {onItemClick(it.name)},
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
                )
        }
    }
}

@Composable
private fun ShopList(
    itemList: List<Item>,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn (modifier = modifier) {
        items (items = itemList, key = {it.name}) {
                item -> ListCard (item = item,
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_small))
                        .clickable { onItemClick(item) })
        }
    }
}

@Composable
private fun ListCard(
    item: Item, modifier: Modifier = Modifier
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
                if (item.products.size > 0) {
                    val numberOfCheckedOut = item.products.count { it.checkedOut }
                    Text(
                        text = numberOfCheckedOut.toString() + " / " + item.products.size.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                    ListItemButton(expanded = expanded, onClick = { expanded = !expanded })
                }
                if (expanded) {
                    PrintAllProducts(products = item.products,
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
    products: ArrayList<Product>, modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        products.forEach { product ->
            Text(
                text = "${product.name} ${product.category} ${product.quantity} ${if (product.price > 0.0) product.price.toString() else ""}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopListPreview() {
    ShoppingList_SemPracaTheme {
        HomeBody(
            itemList = listOf(
                Item(
                    "Tesco",
                    arrayListOf(Product("Jablko", "Fruit", 5, 0.00, false),
                        Product("Mlieko", "Other", 6, 1.20, true)
                    )
                )
            ), onItemClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShopListEmptyPreview() {
    ShoppingList_SemPracaTheme {
        HomeBody(itemList = listOf(), onItemClick = {})
    }
}