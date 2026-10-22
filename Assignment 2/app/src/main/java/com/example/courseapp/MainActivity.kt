package com.example.courseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider

/** Hosts the course application's Compose interface in a single activity. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = CourseRepository.getInstance(applicationContext)
        val courseViewModel = ViewModelProvider(
            this,
            CourseViewModel.Factory(repository)
        )[CourseViewModel::class.java]
        setContent {
            MaterialTheme {
                CourseScreen(courseViewModel)
            }
        }
    }
}
