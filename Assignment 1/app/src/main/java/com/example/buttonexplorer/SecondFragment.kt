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

    /**
     * Restores the selection from saved state or reads the initial fragment arguments.
     *
     * @param savedInstanceState Previously saved selection, or null on first creation.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedButtonText = savedInstanceState?.getString(SELECTED_TEXT_KEY)
            ?: requireArguments().getString(SELECTED_TEXT_KEY).orEmpty()
    }

    /**
     * Inflates the selection layout using View Binding.
     *
     * @param inflater Inflater used to create the fragment's views.
     * @param container Parent used for layout parameters; the view is not attached here.
     * @param savedInstanceState Previous fragment state, or null on first creation.
     * @return The root view containing the selected text and return button.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Displays the selection and connects the return button to the fragment back stack.
     *
     * @param view The root view returned by onCreateView.
     * @param savedInstanceState Previous fragment state, or null on first creation.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectedText.text = selectedButtonText
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Saves the selected text for restoration when the fragment is recreated.
     *
     * @param outState Bundle in which to store the current selection.
     */
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

        /**
         * Creates a fragment with the selected text stored in a Bundle for recreation.
         *
         * @param buttonText The displayed label of the button pressed in the first fragment.
         * @return A new selection fragment with its arguments populated.
         */
        fun newInstance(buttonText: String): SecondFragment {
            return SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(SELECTED_TEXT_KEY, buttonText)
                }
            }
        }
    }
}
