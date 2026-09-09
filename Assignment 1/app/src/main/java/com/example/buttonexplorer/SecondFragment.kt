package com.example.buttonexplorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.buttonexplorer.databinding.FragmentSecondBinding

/** Displays the selected destination and provides navigation back to the choices. */
class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private var selectedButtonText = ""

    /** Restores the selection from saved state or reads the initial fragment arguments. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedButtonText = savedInstanceState?.getString(SELECTED_TEXT_KEY)
            ?: requireArguments().getString(SELECTED_TEXT_KEY).orEmpty()
    }

    /** Inflates the selection layout using View Binding. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    /** Displays the selection and connects the return button to the fragment back stack. */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectedText.text = selectedButtonText
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /** Saves the selected text for restoration when the fragment is recreated. */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SELECTED_TEXT_KEY, selectedButtonText)
    }

    /** Clears the binding to avoid retaining the destroyed view. */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SELECTED_TEXT_KEY = "selected_button_text"

        /** Creates a fragment with the selected text stored in a Bundle for recreation. */
        fun newInstance(buttonText: String): SecondFragment {
            return SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(SELECTED_TEXT_KEY, buttonText)
                }
            }
        }
    }
}
