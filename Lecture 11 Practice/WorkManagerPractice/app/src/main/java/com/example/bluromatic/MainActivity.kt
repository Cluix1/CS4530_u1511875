package com.example.bluromatic

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.bluromatic.workers.*

class MainActivity : ComponentActivity() {
    private lateinit var manager: WorkManager
    private var progress by mutableStateOf("Choose a blur level and press Start")
    private var imageUri by mutableStateOf<String?>(null)
    private var running by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = WorkManager.getInstance(applicationContext)
        manager.getWorkInfosForUniqueWorkLiveData(WORK_NAME).observe(this) { info ->
            if (info.isNullOrEmpty()) return@observe
            val saved = info.lastOrNull { it.tags.contains(SaveImageToFileWorker::class.java.name) }
            val blur = info.lastOrNull { it.tags.contains(BlurWorker::class.java.name) }
            when {
                saved?.state == WorkInfo.State.SUCCEEDED -> {
                    imageUri = saved.outputData.getString(KEY_IMAGE_URI)
                    progress = "Image blurred and saved!"
                    running = false
                }
                info.any { it.state == WorkInfo.State.FAILED } -> {
                    progress = "Something went wrong. Try again."
                    running = false
                }
                info.any { it.state == WorkInfo.State.CANCELLED } -> {
                    progress = "Work cancelled"
                    running = false
                }
                blur?.state == WorkInfo.State.RUNNING -> {
                    progress = "Blurring image in the background..."
                    running = true
                }
                info.any { !it.state.isFinished } -> {
                    progress = "Processing image..."
                    running = true
                }
            }
        }
        setContent {
            MaterialTheme {
                var level by remember { mutableIntStateOf(1) }
                val uri = imageUri
                val resultImage = remember(uri) {
                    runCatching {
                        uri?.let { text ->
                            contentResolver.openInputStream(Uri.parse(text))?.use {
                                BitmapFactory.decodeStream(it)?.asImageBitmap()
                            }
                        }
                    }.getOrNull()
                }
                Surface(Modifier.fillMaxSize()) {
                    Column(
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Blur-O-Matic", style = MaterialTheme.typography.headlineMedium)
                        Text("WorkManager background processing demo")
                        Image(
                            painter = painterResource(R.drawable.android_cupcake),
                            contentDescription = "Original cupcake image",
                            modifier = Modifier.fillMaxWidth().height(230.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text("Select blur amount", style = MaterialTheme.typography.titleMedium)
                        listOf("Slight blur", "Medium blur", "Strong blur").forEachIndexed { index, label ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = level == index + 1, onClick = { level = index + 1 })
                                Text(label)
                            }
                        }
                        Button(enabled = !running, onClick = {
                            imageUri = null
                            progress = "Queued..."
                            running = true
                            val cleanup = OneTimeWorkRequestBuilder<CleanupWorker>().build()
                            val blur = OneTimeWorkRequestBuilder<BlurWorker>()
                                .setInputData(workDataOf(KEY_BLUR_LEVEL to level)).build()
                            val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>().build()
                            manager.beginUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, cleanup)
                                .then(blur).then(save).enqueue()
                        }) { Text(if (running) "Working..." else "Start") }
                        if (running) CircularProgressIndicator()
                        Text(progress)
                        if (resultImage != null) {
                            Text("Blurred image", style = MaterialTheme.typography.titleLarge)
                            Image(
                                bitmap = resultImage,
                                contentDescription = "Blurred cupcake image",
                                modifier = Modifier.fillMaxWidth().height(230.dp),
                                contentScale = ContentScale.Fit
                            )
                            Text("Saved to app storage: final_blurred_image.png")
                        }
                    }
                }
            }
        }
    }
}
