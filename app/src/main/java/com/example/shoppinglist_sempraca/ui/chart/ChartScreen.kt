package com.example.shoppinglist_sempraca.ui.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ShoppingListTopBar
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.navigation.NavigationDestination

object ChartDestination : NavigationDestination {
    override val route = "Habits"
    override val titleRes = R.string.habits
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    chartViewModel: ChartViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    Scaffold(
        topBar = {
            ShoppingListTopBar(
                title = stringResource(ChartDestination.titleRes),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ) { innerPadding ->
        LaunchedEffect(key1 = chartViewModel) {
            chartViewModel.fetchInvisibleItems()
        }

        val averagePrice = chartViewModel.averageTotalPrice()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Average total price: $averagePrice $",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}