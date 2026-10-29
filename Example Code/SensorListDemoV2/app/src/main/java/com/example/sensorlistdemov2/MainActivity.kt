package com.example.sensorlistdemov2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensorlistdemov2.ui.theme.SensorListDemoV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SensorListDemoV2Theme {
                SensorList()
            }
        }
    }
}

@Composable
fun SensorList() {

    val context = LocalContext.current

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val sensors = remember {
        sensorManager.getSensorList(Sensor.TYPE_ALL)
    }


    Column(Modifier.fillMaxWidth().statusBarsPadding().padding(10.dp),
        verticalArrangement = Arrangement.Center) {

        Text("Sensor List", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color.Blue)
        Row{
            LazyColumn {
                items(sensors) { Text("${sensorTypeToString(it.type)} - ${it.power} - ${it.maximumRange}",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold) }
            }
        }
    }
}

fun sensorTypeToString(sensorType: Int): String {
    return when (sensorType) {
        Sensor.TYPE_ACCELEROMETER -> "Accelerometer"
        Sensor.TYPE_AMBIENT_TEMPERATURE -> "Ambient Temperature"
        Sensor.TYPE_GAME_ROTATION_VECTOR -> "Game Rotation Vector"
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "Geomagnetic Rotation Vector"
        Sensor.TYPE_GRAVITY -> "Gravity"
        Sensor.TYPE_GYROSCOPE -> "Gyroscope"
        Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "Gyroscope Uncalibrated"
        Sensor.TYPE_HEART_RATE -> "Heart Rate Monitor"
        Sensor.TYPE_LIGHT -> "Light"
        Sensor.TYPE_LINEAR_ACCELERATION -> "Linear Acceleration"
        Sensor.TYPE_MAGNETIC_FIELD -> "Magnetic Field"
        Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "Magnetic Field Uncalibrated"
        Sensor.TYPE_PRESSURE -> "Orientation"
        Sensor.TYPE_PROXIMITY -> "Proximity"
        Sensor.TYPE_RELATIVE_HUMIDITY -> "Relative Humidity"
        Sensor.TYPE_ROTATION_VECTOR -> "Rotation Vector"
        Sensor.TYPE_SIGNIFICANT_MOTION -> "Significant Motion"
        Sensor.TYPE_STEP_COUNTER -> "Step Counter"
        Sensor.TYPE_STEP_DETECTOR -> "Step Detector"
        else -> "Unknown"
    }
}


