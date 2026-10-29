package com.example.funfacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as FunFactApplication
        val viewModel = ViewModelProvider(
            this,
            FunFactViewModel.Factory(app.repository)
        )[FunFactViewModel::class.java]

        setContent {
            MaterialTheme {
                FunFactScreen(viewModel)
            }
        }
    }
}
