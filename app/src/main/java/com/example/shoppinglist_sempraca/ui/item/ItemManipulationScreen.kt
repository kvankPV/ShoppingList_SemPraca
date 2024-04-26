package com.example.shoppinglist_sempraca.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.shoppinglist_sempraca.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemManipulationScreen(
    itemUiState: ItemUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    isAddingNewItem: Boolean,
) {
    var openBottomSheet by rememberSaveable { mutableStateOf(true) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    var textFieldValue by remember { mutableStateOf(if (isAddingNewItem) "" else itemUiState.itemDetails.name) }

    if (openBottomSheet) {
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false
                               onDismissRequest() },
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {
            Column {
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                        onItemValueChange(itemUiState.itemDetails.copy(name = it))
                    },
                    singleLine = true,
                    label = { Text( text = stringResource(id = R.string.entry_name)
                    )
                    })
                Button(onClick = {
                    onSaveClick()
                    openBottomSheet = false
                    onDismissRequest()
                },
                    enabled = textFieldValue.isNotEmpty()
                    ) {
                    Text(text = stringResource(id = R.string.submit))
                }
            }
        }
    }
}