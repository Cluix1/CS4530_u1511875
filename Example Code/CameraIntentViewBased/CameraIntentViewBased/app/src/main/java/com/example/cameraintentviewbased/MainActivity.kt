package com.example.cameraintentviewbased

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cameraintentviewbased.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            val thumbnailImage: Bitmap?  = extras!!.getParcelable("data", Bitmap::class.java)
            binding.ivPic.setImageBitmap(thumbnailImage)
        } else {
            Log.e("CAMERA APP", "no result")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPic.setOnClickListener {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            try {
                // using the camera activity variable I created
                cameraActivity.launch(cameraIntent)
            } catch(ex: ActivityNotFoundException){
                // Handle error
            }
        }
    }
}