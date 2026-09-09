package com.example.buttonexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.buttonexplorer.databinding.ActivityMainBinding

/**
 * Hosts the choice and selection fragments in a single activity.
 * Fragment transactions control which screen is displayed and allow back navigation.
 */
class MainActivity : AppCompatActivity() {
    /**
     * Initializes the host layout and adjusts its padding for the system bars.
     * Adds the choice screen only when there is no saved activity state to restore.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The binding provides the layout containing the shared fragment container.
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system bar insets so the content remains fully visible.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // FragmentManager restores existing fragments after rotation. Adding another
        // first fragment here would replace the user's restored selection screen.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FirstFragment(), "first_fragment")
                .commit()
        }
    }

    /**
     * Displays the selected button text in the second fragment.
     * Its factory method stores the text in arguments so it survives fragment recreation.
     *
     * @param buttonText The displayed label of the button pressed in the first fragment.
     */
    fun showSelectedButton(buttonText: String) {
        // Recording the replacement on the back stack lets either back control
        // reverse this transaction and restore the choice screen.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SecondFragment.newInstance(buttonText), "second_fragment")
            .addToBackStack(null)
            .commit()
    }
}
