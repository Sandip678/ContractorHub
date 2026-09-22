package com.contractorhub.app.ui.payments

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
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import com.contractorhub.app.data.local.entity.PaymentMethod
import com.contractorhub.app.data.local.relation.SiteListItem
import com.contractorhub.app.databinding.FragmentAddEditClientPaymentBinding
import com.contractorhub.app.ui.expenses.PaymentMethodLabels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditClientPaymentFragment : Fragment() {

    private var _binding: FragmentAddEditClientPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditClientPaymentViewModel by viewModels()
    private var paymentId: String? = null
    private var existingPayment: ClientPaymentEntity? = null
    private var sites: List<SiteListItem> = emptyList()
    private var selectedDate: Long = System.currentTimeMillis()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    private val methodOptions by lazy { PaymentMethodLabels.options(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditClientPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentId = arguments?.getString("paymentId")

        binding.editPaymentMethod.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, methodOptions.map { it.first })
        )
        binding.editPaymentMethod.setText(methodOptions.first().first, false)

        binding.buttonPickPaymentDate.text = dateFormat.format(selectedDate)
        binding.buttonPickPaymentDate.setOnClickListener { showDatePicker() }

        setupClientAutocomplete()
        loadSites()

        binding.buttonSavePayment.setOnClickListener { onSaveClicked() }
        binding.buttonDeletePayment.setOnClickListener { confirmDelete() }

        if (paymentId != null) {
            binding.buttonDeletePayment.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val payment = viewModel.loadPayment(paymentId!!)
                existingPayment = payment
                payment ?: return@launch
                prefillFrom(payment)
            }
        }
    }

    private fun setupClientAutocomplete() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val clients = db.clientDao().getAll().first()
            binding.editPaymentClient.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, clients.map { it.name })
            )
            existingPayment?.clientId?.let { clientId ->
                clients.firstOrNull { it.clientId == clientId }?.name?.let {
                    binding.editPaymentClient.setText(it, false)
                }
            }
        }
    }

    private fun loadSites() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            sites = db.siteDao().getSiteList().first()
            val siteNames = listOf(getString(R.string.bill_no_site)) + sites.map { it.siteName }
            binding.editPaymentSite.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, siteNames)
            )
            if (binding.editPaymentSite.text.isNullOrBlank()) {
                binding.editPaymentSite.setText(getString(R.string.bill_no_site), false)
            }
            existingPayment?.siteId?.let { siteId ->
                sites.firstOrNull { it.siteId == siteId }?.siteName?.let {
                    binding.editPaymentSite.setText(it, false)
                }
            }
        }
    }

    private fun prefillFrom(payment: ClientPaymentEntity) {
        selectedDate = payment.date
        binding.buttonPickPaymentDate.text = dateFormat.format(selectedDate)
        binding.editPaymentAmount.setText(payment.amount.toString())
        binding.editPaymentReference.setText(payment.reference)
        binding.editPaymentNote.setText(payment.note)

        val methodLabel = methodOptions.firstOrNull { it.second == payment.paymentMethod }?.first
            ?: methodOptions.first().first
        binding.editPaymentMethod.setText(methodLabel, false)
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
                binding.buttonPickPaymentDate.text = dateFormat.format(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun onSaveClicked() {
        val amount = binding.editPaymentAmount.text?.toString()?.trim()?.toLongOrNull()
        if (amount == null || amount <= 0) {
            binding.inputLayoutPaymentAmount.error = getString(R.string.payment_error_amount_required)
            return
        }
        binding.inputLayoutPaymentAmount.error = null

        val clientName = binding.editPaymentClient.text?.toString()?.trim()
        val siteName = binding.editPaymentSite.text?.toString()
        val siteId = sites.firstOrNull { it.siteName == siteName }?.siteId

        val methodLabel = binding.editPaymentMethod.text?.toString()
        val methodValue = methodOptions.firstOrNull { it.first == methodLabel }?.second
            ?: PaymentMethod.CASH.name

        val reference = binding.editPaymentReference.text?.toString()?.trim()
        val note = binding.editPaymentNote.text?.toString()?.trim()

        viewModel.savePayment(
            existingPayment = existingPayment,
            clientName = clientName,
            siteId = siteId,
            amount = amount,
            date = selectedDate,
            paymentMethod = methodValue,
            reference = reference,
            note = note,
            onSaved = {
                Toast.makeText(requireContext(), R.string.payment_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val payment = existingPayment ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.payment_delete_confirm_title)
            .setMessage(R.string.payment_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deletePayment(payment) {
                    Toast.makeText(requireContext(), R.string.payment_deleted_toast, Toast.LENGTH_SHORT).show()
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
