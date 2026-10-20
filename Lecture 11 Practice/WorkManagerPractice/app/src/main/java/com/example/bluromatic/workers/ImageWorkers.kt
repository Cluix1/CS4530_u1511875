package com.example.bluromatic.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.bluromatic.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val KEY_BLUR_LEVEL = "KEY_BLUR_LEVEL"
const val WORK_NAME = "blur_image_work"

private fun saveBitmap(context: Context, image: Bitmap): Uri {
    val dir = File(context.filesDir, "blur_filter_outputs").apply { mkdirs() }
    val file = File(dir, "blur_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
    return Uri.fromFile(file)
}

private fun readBitmap(context: Context, uriText: String?): Bitmap {
    return if (uriText.isNullOrBlank()) {
        BitmapFactory.decodeResource(context.resources, R.drawable.android_cupcake)
            ?: error("Source image is missing")
    } else {
        val uri = Uri.parse(uriText)
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            ?: error("Unable to read image: $uri")
    }
}

class CleanupWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            File(applicationContext.filesDir, "blur_filter_outputs")
                .listFiles()?.forEach { if (it.isFile) it.delete() }
            Result.success()
        } catch (e: Exception) {
            Log.e("CleanupWorker", "Unable to clean output directory", e)
            Result.failure()
        }
    }
}

class BlurWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            val level = inputData.getInt(KEY_BLUR_LEVEL, 1).coerceIn(1, 3)
            val source = readBitmap(applicationContext, inputData.getString(KEY_IMAGE_URI))
            var current = source.copy(Bitmap.Config.ARGB_8888, true)
            source.recycle()
            repeat(level) {
                val next = boxBlur(current, 7)
                current.recycle()
                current = next
            }
            val uri = saveBitmap(applicationContext, current)
            current.recycle()
            Result.success(Data.Builder().putString(KEY_IMAGE_URI, uri.toString()).build())
        } catch (e: Exception) {
            Log.e("BlurWorker", "Unable to blur image", e)
            Result.failure()
        }
    }
}

class SaveImageToFileWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val source = readBitmap(applicationContext, inputData.getString(KEY_IMAGE_URI))
            val destination = File(applicationContext.filesDir, "final_blurred_image.png")
            FileOutputStream(destination).use { source.compress(Bitmap.CompressFormat.PNG, 100, it) }
            source.recycle()
            Result.success(Data.Builder().putString(KEY_IMAGE_URI, Uri.fromFile(destination).toString()).build())
        } catch (e: Exception) {
            Log.e("SaveImageWorker", "Unable to save image", e)
            Result.failure()
        }
    }
}

private fun boxBlur(bitmap: Bitmap, radius: Int): Bitmap {
    val w = bitmap.width
    val h = bitmap.height
    val pixels = IntArray(w * h)
    bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
    val horizontal = IntArray(w * h)
    val result = IntArray(w * h)
    for (y in 0 until h) {
        for (x in 0 until w) {
            var a = 0; var r = 0; var g = 0; var b = 0; var count = 0
            for (dx in -radius..radius) {
                val xx = (x + dx).coerceIn(0, w - 1)
                val p = pixels[y * w + xx]
                a += p ushr 24 and 255; r += p ushr 16 and 255
                g += p ushr 8 and 255; b += p and 255; count++
            }
            horizontal[y * w + x] = ((a / count) shl 24) or ((r / count) shl 16) or
                ((g / count) shl 8) or (b / count)
        }
    }
    for (y in 0 until h) {
        for (x in 0 until w) {
            var a = 0; var r = 0; var g = 0; var b = 0; var count = 0
            for (dy in -radius..radius) {
                val yy = (y + dy).coerceIn(0, h - 1)
                val p = horizontal[yy * w + x]
                a += p ushr 24 and 255; r += p ushr 16 and 255
                g += p ushr 8 and 255; b += p and 255; count++
            }
            result[y * w + x] = ((a / count) shl 24) or ((r / count) shl 16) or
                ((g / count) shl 8) or (b / count)
        }
    }
    return Bitmap.createBitmap(result, w, h, Bitmap.Config.ARGB_8888)
}
