package com.example.buttonexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buttonexplorer.databinding.ActivityMainBinding

/** Hosts the destination screens and manages fragment navigation. */
class MainActivity : AppCompatActivity() {
    /** Sets up the activity layout and adds the first fragment on a fresh launch. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system bar insets so the content remains fully visible.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FragmentManager restores the current screen and back stack after rotation.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FirstFragment(), "first_fragment")
                .commit()
        }
    }

    /** Opens the selected destination and preserves the first screen on the back stack. */
    fun showSelectedButton(buttonText: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SecondFragment.newInstance(buttonText), "second_fragment")
            .addToBackStack(null)
            .commit()
    }
}
