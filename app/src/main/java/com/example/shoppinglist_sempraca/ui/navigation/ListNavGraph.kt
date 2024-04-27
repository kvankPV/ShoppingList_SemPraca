package com.example.shoppinglist_sempraca.ui.navigation

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ui.archive.ArchiveScreen
import com.example.shoppinglist_sempraca.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AppScaffold(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(dimensionResource(id = R.dimen.topBar_divider)),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = NavigationBarDefaults.Elevation,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                Screen.entries.forEach { screen ->
                    IconButton(
                        onClick = { navController.navigate(screen.route) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(id = screen.title)
                        )
                    }
                }
            }
        }
    ) {
        ListNavHost(navController, modifier)
    }
}

@Composable
fun ListNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
        modifier = modifier
    ) {
        composable(route = Screen.List.route) {
            HomeScreen()
        }
        // Add other screens here
        composable(route = Screen.Archive.route) {
            ArchiveScreen()
        }
    }
}

enum class Screen(val route: String, val icon: ImageVector, @StringRes val title: Int) {
    List("list", Icons.Filled.Home, R.string.app_name),
    Archive("archive", Icons.Filled.MailOutline, R.string.archive),
    Trends("trends", Icons.Filled.Info, R.string.trends)
}