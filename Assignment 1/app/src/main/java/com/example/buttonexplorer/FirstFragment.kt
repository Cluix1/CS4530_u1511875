package com.example.buttonexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.buttonexplorer.databinding.FragmentFirstBinding

/** Displays five destinations and forwards the selected button text to the host activity. */
class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    /**
     * Inflates the destination layout using View Binding.
     *
     * @param inflater Inflater used to create the fragment's views.
     * @param container Parent used for layout parameters; the view is not attached here.
     * @param savedInstanceState Previous fragment state, or null on first creation.
     * @return The root view containing the five destination buttons.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Configures the buttons once the fragment view is available.
     *
     * @param view The root view returned by onCreateView.
     * @param savedInstanceState Previous fragment state, or null on first creation.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureButtons()
    }

    /** Assigns navigation and confirmation feedback to each button without repeating setup code. */
    private fun configureButtons() {
        val buttons = listOf(
            binding.mountainsButton,
            binding.oceanButton,
            binding.forestButton,
            binding.desertButton,
            binding.cityButton
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                val buttonText = button.text.toString()
                (requireActivity() as MainActivity).showSelectedButton(buttonText)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.selection_confirmation, buttonText),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /** Releases the binding because the fragment may remain on the back stack without its view. */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
