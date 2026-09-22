package com.contractorhub.app.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.databinding.FragmentPlaceholderBinding

/**
 * Temporary stand-in for modules that get built in later phases
 * (Sites → Phase 3, Labour → Phase 5, Materials → Phase 7, ...).
 * PART 89 — Build Strategy: Foundation → Build → Test → Freeze → Next Module.
 * हा फक्त navigation graph compile आणि chalu राहावा यासाठी आहे — प्रत्यक्ष module
 * आल्यावर संबंधित nav_graph destination स्वतःच्या dedicated Fragment कडे बदलायचा.
 */
class PlaceholderFragment : Fragment() {

    private var _binding: FragmentPlaceholderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceholderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val label = findNavController().currentDestination?.label ?: ""
        binding.textPlaceholderLabel.text = "$label — पुढच्या phase मध्ये तयार होईल"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
