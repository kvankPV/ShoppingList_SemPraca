package com.example.shoppinglist_sempraca

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.shoppinglist_sempraca.ui.navigation.ListNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun ShoppingListApp(
    navController: NavHostController = rememberNavController()
) {
    ListNavHost(navController = navController)
}

/**
 * App bar to display title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListTopBar(
    modifier: Modifier = Modifier,
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
}