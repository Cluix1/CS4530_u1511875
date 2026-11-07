package com.example.marblegame

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface

class GravitySensorRepository(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        ?: sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var onReading: ((Float, Float) -> Unit)? = null
    private var rotation = Surface.ROTATION_0
    private var filteredX = 0f
    private var filteredY = 0f
    private var hasReading = false

    fun start(displayRotation: Int, onReading: (Float, Float) -> Unit): String {
        rotation = displayRotation
        this.onReading = onReading
        hasReading = false
        val selectedSensor = sensor ?: return "No gravity sensor or accelerometer available"
        val registered = sensorManager.registerListener(this, selectedSensor, SensorManager.SENSOR_DELAY_GAME)
        if (!registered) return "Could not start the motion sensor"
        return if (selectedSensor.type == Sensor.TYPE_GRAVITY) {
            "Gravity sensor"
        } else {
            "Accelerometer (smoothed)"
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        onReading = null
        hasReading = false
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && hasReading) {
            // Smooth out quick changes when using the emulator fallback.
            filteredX = 0.8f * filteredX + 0.2f * x
            filteredY = 0.8f * filteredY + 0.2f * y
        } else {
            filteredX = x
            filteredY = y
        }
        hasReading = true
        // Keep tilt directions correct when the screen rotates.
        when (rotation) {
            Surface.ROTATION_90 -> onReading?.invoke(-filteredY, filteredX)
            Surface.ROTATION_180 -> onReading?.invoke(-filteredX, -filteredY)
            Surface.ROTATION_270 -> onReading?.invoke(filteredY, -filteredX)
            else -> onReading?.invoke(filteredX, filteredY)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
