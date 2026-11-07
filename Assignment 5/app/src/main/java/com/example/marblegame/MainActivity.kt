package com.example.marblegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    private lateinit var sensorRepository: GravitySensorRepository
    private lateinit var viewModel: MarbleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sensorRepository = GravitySensorRepository(this)
        viewModel = ViewModelProvider(this)[MarbleViewModel::class.java]
        setContent {
            MaterialTheme {
                MarbleScreen(viewModel)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        val rotation = windowManager.defaultDisplay.rotation
        viewModel.setSensorStatus(sensorRepository.start(rotation, viewModel::updateGravity))
    }

    override fun onPause() {
        sensorRepository.stop()
        viewModel.stop()
        super.onPause()
    }
}
