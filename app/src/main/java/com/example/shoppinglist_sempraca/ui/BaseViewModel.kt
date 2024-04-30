package com.example.shoppinglist_sempraca.ui

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {
    private val _voiceInput = MutableStateFlow("")
    val voiceInput = _voiceInput.asStateFlow()

    fun handleVoiceInputResult(data: Intent?) {
        val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        if (!matches.isNullOrEmpty()) {
            val voiceInput = matches[0]
            viewModelScope.launch {
                _voiceInput.emit(voiceInput)
            }
        }
    }
}