package com.contractorhub.app.ui.materials

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
import com.contractorhub.app.databinding.FragmentMaterialListBinding
import kotlinx.coroutines.launch

class MaterialListFragment : Fragment() {

    private var _binding: FragmentMaterialListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MaterialListViewModel by viewModels()
    private lateinit var adapter: MaterialListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MaterialListAdapter { material ->
            findNavController().navigate(
                R.id.materialProfileFragment,
                bundleOf("materialId" to material.materialId)
            )
        }
        binding.recyclerMaterials.adapter = adapter

        binding.fabAddMaterial.setOnClickListener { openAddMaterial() }
        binding.buttonEmptyAddMaterial.setOnClickListener { openAddMaterial() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.materials.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerMaterials.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddMaterial() {
        findNavController().navigate(
            R.id.addEditMaterialFragment,
            bundleOf("materialId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerMaterials.adapter = null
        _binding = null
    }
}
