package com.contractorhub.app.ui.expenses

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
import com.contractorhub.app.databinding.FragmentExpenseListBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseListViewModel by viewModels()
    private lateinit var adapter: ExpenseListAdapter
    private var siteNames: Map<String, String> = emptyMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExpenseListAdapter(siteNameOf = { id -> id?.let { siteNames[it] } }) { expense ->
            findNavController().navigate(
                R.id.addEditExpenseFragment,
                bundleOf("expenseId" to expense.expenseId)
            )
        }
        binding.recyclerExpenses.adapter = adapter

        binding.fabAddExpense.setOnClickListener { openAddExpense() }
        binding.buttonEmptyAddExpense.setOnClickListener { openAddExpense() }

        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            siteNames = db.siteDao().getSiteList().first().associate { it.siteId to it.siteName }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.expenses.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerExpenses.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddExpense() {
        findNavController().navigate(
            R.id.addEditExpenseFragment,
            bundleOf("expenseId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerExpenses.adapter = null
        _binding = null
    }
}
