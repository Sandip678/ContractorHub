package com.contractorhub.app.ui.bills

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
import com.contractorhub.app.data.local.entity.BillStatus
import com.contractorhub.app.data.local.entity.status
import com.contractorhub.app.databinding.FragmentBillListBinding
import kotlinx.coroutines.launch

class BillListFragment : Fragment() {

    private var _binding: FragmentBillListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BillListViewModel by viewModels()
    private lateinit var adapter: BillListAdapter
    private var supplierNames: Map<String, String> = emptyMap()
    private var currentFilter: BillStatus? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BillListAdapter(
            supplierNameOf = { id -> supplierNames[id] ?: getString(R.string.bill_no_site) }
        ) { bill ->
            findNavController().navigate(
                R.id.addEditBillFragment,
                bundleOf("billId" to bill.billId)
            )
        }
        binding.recyclerBills.adapter = adapter

        binding.fabAddBill.setOnClickListener { openAddBill() }
        binding.buttonEmptyAddBill.setOnClickListener { openAddBill() }

        binding.chipGroupBillFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when (checkedIds.firstOrNull()) {
                R.id.chip_filter_paid -> BillStatus.PAID
                R.id.chip_filter_unpaid -> BillStatus.UNPAID
                R.id.chip_filter_partial -> BillStatus.PARTIAL
                else -> null
            }
            applyFilter()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suppliers.collect { list ->
                    supplierNames = list.associate { it.supplierId to it.name }
                    applyFilter()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bills.collect { applyFilter() }
            }
        }
    }

    private fun applyFilter() {
        val allBills = viewModel.bills.value
        val filtered = currentFilter?.let { filter -> allBills.filter { it.status() == filter } } ?: allBills
        adapter.submitList(filtered)
        binding.layoutEmptyState.isVisible = filtered.isEmpty()
        binding.recyclerBills.isVisible = filtered.isNotEmpty()
    }

    private fun openAddBill() {
        findNavController().navigate(
            R.id.addEditBillFragment,
            bundleOf("billId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerBills.adapter = null
        _binding = null
    }
}
