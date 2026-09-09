package com.example.buttonexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.buttonexplorer.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureButtons()
    }

    private fun configureButtons() {
        // Share one listener pattern so every button passes its own displayed text.
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

    override fun onDestroyView() {
        super.onDestroyView()
        // A fragment can outlive its view while it is on the back stack.
        _binding = null
    }
}
