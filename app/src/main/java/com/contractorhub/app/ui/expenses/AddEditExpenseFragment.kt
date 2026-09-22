package com.contractorhub.app.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ExpenseCategory
import com.contractorhub.app.data.local.entity.ExpenseEntity
import com.contractorhub.app.data.local.entity.PaymentMethod
import com.contractorhub.app.data.local.relation.SiteListItem
import com.contractorhub.app.databinding.FragmentAddEditExpenseBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditExpenseFragment : Fragment() {

    private var _binding: FragmentAddEditExpenseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditExpenseViewModel by viewModels()
    private var expenseId: String? = null
    private var existingExpense: ExpenseEntity? = null
    private var sites: List<SiteListItem> = emptyList()
    private var selectedDate: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val categoryOptions by lazy { ExpenseCategoryLabels.options(requireContext()) }
    private val methodOptions by lazy { PaymentMethodLabels.options(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expenseId = arguments?.getString("expenseId")

        binding.editExpenseCategory.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categoryOptions.map { it.first })
        )
        binding.editExpenseCategory.setText(categoryOptions.first().first, false)

        binding.editExpensePaymentMethod.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, methodOptions.map { it.first })
        )
        binding.editExpensePaymentMethod.setText(methodOptions.first().first, false)

        binding.buttonPickExpenseDate.text = dateFormat.format(selectedDate)
        binding.buttonPickExpenseDate.setOnClickListener { showDatePicker() }

        loadSites()

        binding.buttonSaveExpense.setOnClickListener { onSaveClicked() }
        binding.buttonDeleteExpense.setOnClickListener { confirmDelete() }

        if (expenseId != null) {
            binding.buttonDeleteExpense.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val expense = viewModel.loadExpense(expenseId!!)
                existingExpense = expense
                expense ?: return@launch
                prefillFrom(expense)
            }
        }
    }

    private fun loadSites() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            sites = db.siteDao().getSiteList().first()
            val siteNames = listOf(getString(R.string.bill_no_site)) + sites.map { it.siteName }
            binding.editExpenseSite.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, siteNames)
            )
            if (binding.editExpenseSite.text.isNullOrBlank()) {
                binding.editExpenseSite.setText(getString(R.string.bill_no_site), false)
            }
            existingExpense?.siteId?.let { siteId ->
                sites.firstOrNull { it.siteId == siteId }?.siteName?.let {
                    binding.editExpenseSite.setText(it, false)
                }
            }
        }
    }

    private fun prefillFrom(expense: ExpenseEntity) {
        selectedDate = expense.date
        binding.buttonPickExpenseDate.text = dateFormat.format(selectedDate)
        binding.editExpenseAmount.setText(expense.amount.toString())
        binding.editExpenseNote.setText(expense.note)

        val categoryLabel = categoryOptions.firstOrNull { it.second == expense.category }?.first
            ?: categoryOptions.first().first
        binding.editExpenseCategory.setText(categoryLabel, false)

        val methodLabel = methodOptions.firstOrNull { it.second == expense.paymentMethod }?.first
            ?: methodOptions.first().first
        binding.editExpensePaymentMethod.setText(methodLabel, false)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                selectedDate = picked.timeInMillis
                binding.buttonPickExpenseDate.text = dateFormat.format(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun onSaveClicked() {
        val amount = binding.editExpenseAmount.text?.toString()?.trim()?.toLongOrNull()
        if (amount == null || amount <= 0) {
            binding.inputLayoutExpenseAmount.error = getString(R.string.expense_error_amount_required)
            return
        }
        binding.inputLayoutExpenseAmount.error = null

        val categoryLabel = binding.editExpenseCategory.text?.toString()
        val categoryValue = categoryOptions.firstOrNull { it.first == categoryLabel }?.second
            ?: ExpenseCategory.OTHER.name

        val methodLabel = binding.editExpensePaymentMethod.text?.toString()
        val methodValue = methodOptions.firstOrNull { it.first == methodLabel }?.second
            ?: PaymentMethod.CASH.name

        val siteName = binding.editExpenseSite.text?.toString()
        val siteId = sites.firstOrNull { it.siteName == siteName }?.siteId
        val note = binding.editExpenseNote.text?.toString()?.trim()

        viewModel.saveExpense(
            existingExpense = existingExpense,
            siteId = siteId,
            category = categoryValue,
            amount = amount,
            date = selectedDate,
            paymentMethod = methodValue,
            note = note,
            onSaved = {
                Toast.makeText(requireContext(), R.string.expense_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val expense = existingExpense ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.expense_delete_confirm_title)
            .setMessage(R.string.expense_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteExpense(expense) {
                    Toast.makeText(requireContext(), R.string.expense_deleted_toast, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
