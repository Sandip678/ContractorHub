package com.contractorhub.app.ui.payments

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
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.databinding.FragmentClientPaymentListBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClientPaymentListFragment : Fragment() {

    private var _binding: FragmentClientPaymentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ClientPaymentListViewModel by viewModels()
    private lateinit var adapter: ClientPaymentListAdapter
    private var clientNames: Map<String, String> = emptyMap()
    private var siteNames: Map<String, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientPaymentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ClientPaymentListAdapter(
            clientNameOf = { id -> id?.let { clientNames[it] } },
            siteNameOf = { id -> id?.let { siteNames[it] } }
        ) { payment ->
            findNavController().navigate(
                R.id.addEditClientPaymentFragment,
                bundleOf("paymentId" to payment.paymentId)
            )
        }
        binding.recyclerPayments.adapter = adapter

        binding.fabAddPayment.setOnClickListener { openAddPayment() }
        binding.buttonEmptyAddPayment.setOnClickListener { openAddPayment() }

        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            clientNames = db.clientDao().getAll().first().associate { it.clientId to it.name }
            siteNames = db.siteDao().getSiteList().first().associate { it.siteId to it.siteName }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.payments.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerPayments.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddPayment() {
        findNavController().navigate(
            R.id.addEditClientPaymentFragment,
            bundleOf("paymentId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerPayments.adapter = null
        _binding = null
    }
}
