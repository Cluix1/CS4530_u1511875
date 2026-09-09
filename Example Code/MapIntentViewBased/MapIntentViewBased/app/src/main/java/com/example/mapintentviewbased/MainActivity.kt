package com.example.mapintentviewbased

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mapintentviewbased.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSubmit.setOnClickListener {
            val searchString = binding.etSearch.text.toString()
            openMap(searchString)

        }


    }

    fun openMap(searchString: String){

        if (searchString.isBlank()) {
            Toast.makeText(this, "Enter something!", Toast.LENGTH_SHORT).show()
        } else {
            //We have to grab the search term and construct a URI object from it.
            //We'll hardcode WEB's location here
            val searchUri = "geo:40.767778,-111.845205?q=${searchString + " in Salt Lake City"}".toUri()

            //Create the implicit intent
            val mapIntent = Intent(Intent.ACTION_VIEW, searchUri)

            //If there's an activity associated with this intent, launch it
            try{
                startActivity(mapIntent)
            }catch(ex: ActivityNotFoundException){
                //handle errors here
                Toast.makeText(this, "No compatible apps found", Toast.LENGTH_LONG).show()
            }
        }
    }
}