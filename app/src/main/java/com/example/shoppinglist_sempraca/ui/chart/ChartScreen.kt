package com.example.shoppinglist_sempraca.ui.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoppinglist_sempraca.R
import com.example.shoppinglist_sempraca.ShoppingListTopBar
import com.example.shoppinglist_sempraca.ui.AppViewModelProvider
import com.example.shoppinglist_sempraca.ui.navigation.NavigationDestination
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberEndAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.m3.style.m3ChartStyle
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.composed.plus
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.composed.ComposedChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf

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
                .padding(innerPadding)
                .padding(bottom = dimensionResource(id = R.dimen.topBar_divider))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Average total price: $averagePrice $",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
                if (chartViewModel.hasInvisibleItems()) {
                    ChartRender(chartViewModel = chartViewModel, modifier = Modifier.weight(9f))
                }
            }
        }
    }
}

@Composable
fun ChartRender(chartViewModel: ChartViewModel, modifier: Modifier) {

    if (chartViewModel.hasInvisibleItems()) {
        val columnChart = columnChart()
        val lineChart = lineChart()
        val composedChart = remember(columnChart, lineChart) { columnChart + lineChart }

        val composedChartEntryModelProducer = ComposedChartEntryModelProducer.build {
            add(entriesOf(*chartViewModel.getFloatItems().toTypedArray()))
            add(entriesOf(*chartViewModel.getCumulativeAveragesAsFloat().toTypedArray()))
        }

        val integerAxisValueFormatter = AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            value.toInt().toString()
        }

        ProvideChartStyle(m3ChartStyle()) { //at this moment, this does nothing.
            Chart(
                chart = composedChart,
                chartModelProducer = composedChartEntryModelProducer,
                modifier = modifier,
                startAxis = rememberStartAxis(valueFormatter = integerAxisValueFormatter,
                    title = "Product No."),
                bottomAxis = rememberBottomAxis(),
                endAxis = rememberEndAxis(title = "y = Price $", titleComponent = textComponent { this.textSizeSp = 16f },
                    label = null, tick = null, itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = 0)),
                topAxis = rememberTopAxis(title = "x = item No.", titleComponent = textComponent { this.textSizeSp = 16f },
                    label = null, tick = null)
            )
        }
    }
}