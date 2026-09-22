package com.contractorhub.app.ui.labour

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.AttendanceStatus
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import com.contractorhub.app.data.local.entity.LabourTransactionType
import com.contractorhub.app.databinding.DialogAddAttendanceBinding
import com.contractorhub.app.databinding.DialogAddTransactionBinding
import com.contractorhub.app.databinding.FragmentLabourProfileBinding
import com.contractorhub.app.utils.CurrencyFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * PART 16 — Labour Profile. इथेच attendance/transaction add करता येतं, आणि
 * Balance लगेच अपडेट होताना दिसतो (LabourProfileViewModel.uiState मुळे) —
 * PART 7 चं "one entry → automatic update" तत्त्व.
 */
class LabourProfileFragment : Fragment() {

    private var _binding: FragmentLabourProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LabourProfileViewModel by viewModels()
    private lateinit var attendanceAdapter: AttendanceAdapter
    private lateinit var ledgerAdapter: LedgerAdapter

    private var labourId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLabourProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labourId = arguments?.getString("labourId").orEmpty()
        viewModel.setLabourId(labourId)

        attendanceAdapter = AttendanceAdapter { attendance -> confirmDeleteAttendance(attendance) }
        ledgerAdapter = LedgerAdapter { transaction -> confirmDeleteTransaction(transaction) }
        binding.recyclerAttendance.adapter = attendanceAdapter
        binding.recyclerLedger.adapter = ledgerAdapter

        binding.imageEditLabour.setOnClickListener {
            findNavController().navigate(
                R.id.addEditLabourFragment,
                bundleOf("labourId" to labourId)
            )
        }
        binding.buttonAddAttendance.setOnClickListener { showAddAttendanceDialog() }
        binding.buttonAddTransaction.setOnClickListener { showAddTransactionDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: LabourProfileUiState) {
        val labour = state.labour ?: return
        binding.textProfileName.text = labour.name

        val workType = labour.workType?.takeIf { it.isNotBlank() }
        binding.textProfileWorktype.text = if (workType != null) {
            "$workType • ${CurrencyFormatter.format(labour.dailyRate)}/day"
        } else {
            "${CurrencyFormatter.format(labour.dailyRate)}/day"
        }

        val balance = state.balance
        if (balance != null) {
            binding.textBalanceAmount.text = getString(
                R.string.labour_balance_payable_format,
                CurrencyFormatter.format(if (balance.balance < 0) 0 else balance.balance)
            )
            binding.textWorkSummary.text = getString(
                R.string.labour_work_summary_format,
                balance.presentDays,
                balance.halfDays,
                balance.otHours.toString()
            )
            binding.textTotalEarned.text = CurrencyFormatter.format(balance.totalEarned)
            binding.textPaid.text = CurrencyFormatter.format(balance.paid)
            binding.textAdvance.text = CurrencyFormatter.format(balance.advanceTotal)
            binding.textAngavar.text = CurrencyFormatter.format(balance.angavarTotal)
        }

        attendanceAdapter.submitList(state.attendance)
        ledgerAdapter.submitList(state.transactions)
        binding.textAttendanceEmpty.isVisible = state.attendance.isEmpty()
        binding.textLedgerEmpty.isVisible = state.transactions.isEmpty()
    }

    // ---------- Add Attendance dialog ----------

    private fun showAddAttendanceDialog() {
        val dialogBinding = DialogAddAttendanceBinding.inflate(layoutInflater)
        var selectedDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dialogBinding.buttonPickDate.text = dateFormat.format(selectedDate)

        dialogBinding.buttonPickDate.setOnClickListener {
            showDatePicker(selectedDate) { picked ->
                selectedDate = picked
                dialogBinding.buttonPickDate.text = dateFormat.format(selectedDate)
            }
        }

        val statusOptions = AttendanceStatusLabels.options(requireContext())
        dialogBinding.editAttendanceStatus.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statusOptions.map { it.first })
        )
        dialogBinding.editAttendanceStatus.setText(statusOptions.first().first, false)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.attendance_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_save) { _, _ ->
                val statusLabel = dialogBinding.editAttendanceStatus.text?.toString()
                val statusValue = statusOptions.firstOrNull { it.first == statusLabel }?.second
                    ?: AttendanceStatus.PRESENT.name
                val otHours = dialogBinding.editOtHours.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0
                val note = dialogBinding.editAttendanceNote.text?.toString()?.trim()

                viewModel.addAttendance(
                    siteId = null,
                    date = selectedDate,
                    status = statusValue,
                    otHours = otHours,
                    note = note
                )
                Toast.makeText(requireContext(), R.string.attendance_saved_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmDeleteAttendance(attendance: LabourAttendanceEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.attendance_delete_confirm_title)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteAttendance(attendance)
                Toast.makeText(requireContext(), R.string.attendance_deleted_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    // ---------- Add Transaction dialog ----------

    private fun showAddTransactionDialog() {
        val dialogBinding = DialogAddTransactionBinding.inflate(layoutInflater)
        var selectedDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dialogBinding.buttonPickTransactionDate.text = dateFormat.format(selectedDate)

        dialogBinding.buttonPickTransactionDate.setOnClickListener {
            showDatePicker(selectedDate) { picked ->
                selectedDate = picked
                dialogBinding.buttonPickTransactionDate.text = dateFormat.format(selectedDate)
            }
        }

        val typeOptions = LabourTransactionTypeLabels.options(requireContext())
        dialogBinding.editTransactionType.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, typeOptions.map { it.first })
        )
        dialogBinding.editTransactionType.setText(typeOptions.first().first, false)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.transaction_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_save) { _, _ ->
                val amount = dialogBinding.editTransactionAmount.text?.toString()?.trim()?.toLongOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(
                        requireContext(),
                        R.string.transaction_error_amount_required,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val typeLabel = dialogBinding.editTransactionType.text?.toString()
                val typeValue = typeOptions.firstOrNull { it.first == typeLabel }?.second
                    ?: LabourTransactionType.PAYMENT.name
                val note = dialogBinding.editTransactionNote.text?.toString()?.trim()

                viewModel.addTransaction(
                    siteId = null,
                    date = selectedDate,
                    type = typeValue,
                    amount = amount,
                    note = note
                )
                Toast.makeText(requireContext(), R.string.transaction_saved_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmDeleteTransaction(transaction: LabourTransactionEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.transaction_delete_confirm_title)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteTransaction(transaction)
                Toast.makeText(requireContext(), R.string.transaction_deleted_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showDatePicker(initialMillis: Long, onPicked: (Long) -> Unit) {
        val calendar = Calendar.getInstance().apply { timeInMillis = initialMillis }
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val picked = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onPicked(picked.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerAttendance.adapter = null
        binding.recyclerLedger.adapter = null
        _binding = null
    }
}
