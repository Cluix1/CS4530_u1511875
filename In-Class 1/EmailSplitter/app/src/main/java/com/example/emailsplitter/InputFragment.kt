package com.example.emailsplitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.emailsplitter.databinding.FragmentInputBinding

class InputFragment : Fragment() {
    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.splitButton.setOnClickListener {
            val pieces = binding.emailInput.text.toString().trim().split('@')
            if (pieces.size != 2 || pieces.any(String::isBlank)) {
                Toast.makeText(requireContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(USERNAME_KEY to pieces[0], DOMAIN_KEY to pieces[1])
            )
            Toast.makeText(requireContext(), R.string.data_passed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "email_result"
        const val USERNAME_KEY = "username"
        const val DOMAIN_KEY = "domain"
    }
}
