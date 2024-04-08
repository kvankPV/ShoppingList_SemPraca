package com.example.shoppinglist_sempraca.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shoppinglist_sempraca.ui.home.HomeDestination
import com.example.shoppinglist_sempraca.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun ListNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        //Home
        composable(route = HomeDestination.route) {
            HomeScreen()
        }
    }
}