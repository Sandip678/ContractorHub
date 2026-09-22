package com.contractorhub.app.ui.labour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.databinding.FragmentLabourListBinding
import kotlinx.coroutines.launch

class LabourListFragment : Fragment() {

    private var _binding: FragmentLabourListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LabourListViewModel by viewModels()
    private lateinit var adapter: LabourListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLabourListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LabourListAdapter { labour ->
            findNavController().navigate(
                R.id.labourProfileFragment,
                bundleOf("labourId" to labour.labourId)
            )
        }
        binding.recyclerLabour.adapter = adapter

        binding.fabAddLabour.setOnClickListener { openAddLabour() }
        binding.buttonEmptyAddLabour.setOnClickListener { openAddLabour() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.labourList.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerLabour.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddLabour() {
        findNavController().navigate(
            R.id.addEditLabourFragment,
            bundleOf("labourId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerLabour.adapter = null
        _binding = null
    }
}
