package com.contractorhub.app.ui.clients

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
import com.contractorhub.app.databinding.FragmentClientsBinding
import kotlinx.coroutines.launch

class ClientsFragment : Fragment() {

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientsViewModel by viewModels()
    private lateinit var adapter: ClientListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ClientListAdapter { client ->
            findNavController().navigate(
                R.id.addEditClientFragment,
                bundleOf("clientId" to client.clientId)
            )
        }
        binding.recyclerClients.adapter = adapter

        binding.fabAddClient.setOnClickListener { openAddClient() }
        binding.buttonEmptyAddClient.setOnClickListener { openAddClient() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clients.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerClients.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddClient() {
        findNavController().navigate(
            R.id.addEditClientFragment,
            bundleOf("clientId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerClients.adapter = null
        _binding = null
    }
}
