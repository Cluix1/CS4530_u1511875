package com.example.marblegame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MarbleViewModel : ViewModel() {
    private val physics = MarblePhysics()
    private var gravityX = 0f
    private var gravityY = 0f

    var position by mutableStateOf(MarblePosition())
        private set
    var sensorStatus by mutableStateOf("Waiting for sensor")
        private set

    fun updateSensorStatus(status: String) {
        sensorStatus = status
    }

    fun updateGravity(x: Float, y: Float) {
        gravityX = x
        gravityY = y
    }

    fun setBounds(maxX: Float, maxY: Float) {
        physics.setBounds(maxX, maxY)
        position = physics.position
    }

    fun updateFrame(dt: Float) {
        physics.update(dt, gravityX, gravityY)
        position = physics.position
    }

    fun reset() {
        physics.reset()
        position = physics.position
    }

    fun stop() {
        updateGravity(0f, 0f)
        physics.stop()
    }
}
