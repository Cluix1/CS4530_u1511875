package com.example.buttonexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.buttonexplorer.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private var selectedButtonText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedButtonText = savedInstanceState?.getString(SELECTED_TEXT_KEY)
            ?: requireArguments().getString(SELECTED_TEXT_KEY).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectedText.text = selectedButtonText
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SELECTED_TEXT_KEY, selectedButtonText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SELECTED_TEXT_KEY = "selected_button_text"

        fun newInstance(buttonText: String): SecondFragment {
            // Arguments survive fragment recreation, unlike a custom constructor.
            return SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(SELECTED_TEXT_KEY, buttonText)
                }
            }
        }
    }
}
