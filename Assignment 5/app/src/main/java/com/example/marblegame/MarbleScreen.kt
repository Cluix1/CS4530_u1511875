package com.example.marblegame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.isActive

@Composable
fun MarbleScreen(viewModel: MarbleViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            var previousFrame = 0L
            while (isActive) {
                withFrameNanos { frame ->
                    if (previousFrame != 0L) {
                        viewModel.updateFrame((frame - previousFrame) / 1_000_000_000f)
                    }
                    previousFrame = frame
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.safeDrawingPadding().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Marble Game", style = MaterialTheme.typography.headlineMedium)
            Text("Tilt your phone to roll the marble.")
            Text(viewModel.sensorStatus, style = MaterialTheme.typography.bodySmall)
            Button(onClick = viewModel::reset) {
                Text("Reset marble")
            }
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFE3EDF5))
            ) {
                val marbleSize = minOf(40.dp, maxWidth, maxHeight)
                val maxX = (maxWidth - marbleSize).value.coerceAtLeast(0f)
                val maxY = (maxHeight - marbleSize).value.coerceAtLeast(0f)
                LaunchedEffect(maxX, maxY) {
                    viewModel.setBounds(maxX, maxY)
                }
                val position = viewModel.position
                Box(
                    modifier = Modifier
                        .offset(
                            x = position.x.coerceIn(0f, maxX).dp,
                            y = position.y.coerceIn(0f, maxY).dp
                        )
                        .size(marbleSize)
                        .background(Color(0xFF1565C0), CircleShape)
                        .semantics { contentDescription = "Marble" }
                )
            }
        }
    }
}
