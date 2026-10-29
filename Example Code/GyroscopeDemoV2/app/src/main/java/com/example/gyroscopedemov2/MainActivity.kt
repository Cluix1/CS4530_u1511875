package com.example.gyroscopedemov2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gyroscopedemov2.ui.theme.GyroscopeDemoV2Theme
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GyroscopeDemoV2Theme {
                Gyroscope()
            }
        }
    }
}

@Composable
fun Gyroscope() {

    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    if (gyroscope == null) {
        Text("No gyroscope available on this device")
        return
    }

    val gyroFlow = sensorManager.gyroFlow(gyroscope)
    val gyroReading by gyroFlow.collectAsStateWithLifecycle(GyroReading(0f, 0f, 0f))

    Surface(modifier = Modifier.fillMaxSize().statusBarsPadding(), color = MaterialTheme.colorScheme.background) {
        Text("Gyro reading: ${gyroReading.x}, ${gyroReading.y}, ${gyroReading.z}", fontSize = 20.sp)
    }
}

data class GyroReading(val x: Float, val y: Float, val z: Float)

fun SensorManager.gyroFlow(gyroscope: Sensor): Flow<GyroReading> = channelFlow {
    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            trySendBlocking(GyroReading(event.values[0], event.values[1], event.values[2]))
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }
    registerListener(listener, gyroscope, SensorManager.SENSOR_DELAY_UI)
    awaitClose {
        unregisterListener(listener)
    }
}