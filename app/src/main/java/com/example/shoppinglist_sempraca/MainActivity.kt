package com.example.shoppinglist_sempraca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.shoppinglist_sempraca.ui.theme.ShoppingList_SemPracaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingList_SemPracaTheme {
                ShoppingScreen()
            }
        }
    }
}