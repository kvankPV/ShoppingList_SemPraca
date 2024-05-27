package com.example.shoppinglist_sempraca.ui.item

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.example.shoppinglist_sempraca.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemManipulationScreen(
    itemUiState: ItemUiState,
    onItemValueChange: (ItemDetails) -> Unit,
    onSaveClick: () -> Unit,
    onDismissRequest: () -> Unit,
    isAddingNewItem: Boolean,
    viewModel: ItemManipulationViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    var openBottomSheet by rememberSaveable { mutableStateOf(true) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )

    var textFieldValue by rememberSaveable { mutableStateOf(if (isAddingNewItem) "" else itemUiState.itemDetails.name) }

    val voiceInput by viewModel.voiceInput.collectAsState()

    if (voiceInput != textFieldValue && voiceInput.isNotEmpty()) {
        textFieldValue = voiceInput
        onItemValueChange(itemUiState.itemDetails.copy(name = voiceInput))
    }

    if (openBottomSheet) {
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch {
                bottomSheetState.hide()
            }
                openBottomSheet = false
                onDismissRequest()},
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {
            val voiceInputLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.handleVoiceInputResult(result.data)
                }
            }
            Column {
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                        onItemValueChange(itemUiState.itemDetails.copy(name = it))
                    },
                    singleLine = true,
                    label = { Text( text = stringResource(id = R.string.entry_name)
                    ) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val voiceInputIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
                            }
                            voiceInputLauncher.launch(voiceInputIntent)
                        }) {
                            Icon(ImageVector.vectorResource(id = R.drawable.baseline_mic_24), contentDescription = stringResource(id = R.string.voice_input))
                        }
                    })
                Button(onClick = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                    }
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