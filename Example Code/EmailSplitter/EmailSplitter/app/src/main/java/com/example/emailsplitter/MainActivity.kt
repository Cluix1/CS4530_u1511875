package com.example.emailsplitter

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.emailsplitter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {

            val email = binding.emailInput.text.toString()
            val pieces = email.split('@')

            if (pieces.size != 2 || pieces.any(String::isEmpty)) {

                Toast.makeText(
                    this,
                    "Invalid email!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val username = pieces[0]
                val domain = pieces[1]

                binding.userView.text = username
                binding.domainView.text = domain
            }
        }
    }
}