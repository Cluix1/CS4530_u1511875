package com.example.jetpackdemo


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.PasswordVisualTransformation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(Modifier.statusBarsPadding()) {
                JPCPractice()
                Counter()
            }
        }
    }
}

@Composable
fun JPCPractice() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Normally, colors should be defined in the theme rather than here.
   // This is only done directly for demonstration purposes.
    val lightRed = Color(0xFFFFCDD2)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .background(lightRed),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Username",
            fontSize = 16.sp
        )
        TextField(value = username,
            onValueChange = { username = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Text(
            text = "Password",
            fontSize = 16.sp,
            modifier = Modifier
                .padding(top = 16.dp)
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                    // Handle login
                }
            ) {
                Text("Submit")
            }

            Button(
                onClick = {
                    username = ""
                    password = ""
                }
            ) {
                Text("Clear")
            }
        }
    }
}


@Composable
fun Counter()
{
    Row (Modifier.padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween){
        var count by remember { mutableStateOf(0) }
        var text by remember { mutableStateOf("") }

        Button (onClick={count++})
        {
            Text("Count: $count")
        }

        Spacer(modifier = Modifier.width(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text=it },
            label = { Text("Name") }
        )

    }
}
