package com.example.textconcatenator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ConcatenatorScreen()
                }
            }
        }
    }
}

@Composable
fun ConcatenatorScreen() {
    var firstText by rememberSaveable { mutableStateOf("") }
    var secondText by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Text Concatenator",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextField(
            value = firstText,
            onValueChange = { firstText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("First text") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = secondText,
            onValueChange = { secondText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Second text") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = { result = firstText + secondText }) {
            Text("Concatenate")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Result: $result",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ConcatenatorPreview() {
    MaterialTheme {
        ConcatenatorScreen()
    }
}
