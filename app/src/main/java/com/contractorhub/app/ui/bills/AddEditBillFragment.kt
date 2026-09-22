package com.contractorhub.app.ui.bills

import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.BillEntity
import com.contractorhub.app.data.local.relation.SiteListItem
import com.contractorhub.app.data.repository.PaymentRepository
import com.contractorhub.app.data.repository.SiteRepository
import com.contractorhub.app.databinding.DialogBillPaymentBinding
import com.contractorhub.app.databinding.FragmentAddEditBillBinding
import com.contractorhub.app.ui.expenses.PaymentMethodLabels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * PART 22 — Bill Scanner review step + PART 21 manual bill entry, एकाच फॉर्मने.
 * PART 24 — Duplicate Bill Check save करण्यापूर्वी.
 */
class AddEditBillFragment : Fragment() {

    private var _binding: FragmentAddEditBillBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditBillViewModel by viewModels()
    private var billId: String? = null
    private var existingBill: BillEntity? = null
    private var pickedImagePath: String? = null
    private var selectedDate: Long = System.currentTimeMillis()
    private var sites: List<SiteListItem> = emptyList()

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billId = arguments?.getString("billId")

        binding.buttonPickBillDate.text = dateFormat.format(selectedDate)
        binding.buttonPickBillDate.setOnClickListener { showDatePicker() }

        setupFragmentResultListener()
        setupSupplierAutocomplete()
        loadSites()

        binding.buttonScanBill.setOnClickListener { openCamera() }
        binding.buttonSaveBill.setOnClickListener { onSaveClicked(force = false) }
        binding.buttonDeleteBill.setOnClickListener { confirmDelete() }
        binding.buttonRecordBillPayment.setOnClickListener { showBillPaymentDialog() }

        if (billId != null) {
            binding.buttonDeleteBill.visibility = View.VISIBLE
            binding.buttonRecordBillPayment.isVisible = true
            viewLifecycleOwner.lifecycleScope.launch {
                val bill = viewModel.loadBill(billId!!)
                existingBill = bill
                bill ?: return@launch
                prefillFrom(bill)
            }
        }
    }

    private fun showBillPaymentDialog() {
        val bill = existingBill ?: return
        val dialogBinding = DialogBillPaymentBinding.inflate(layoutInflater)
        var selectedPaymentDate = System.currentTimeMillis()
        dialogBinding.buttonPickBillPaymentDate.text = dateFormat.format(selectedPaymentDate)

        dialogBinding.buttonPickBillPaymentDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedPaymentDate }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val picked = Calendar.getInstance().apply {
                        set(year, month, day, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    selectedPaymentDate = picked.timeInMillis
                    dialogBinding.buttonPickBillPaymentDate.text = dateFormat.format(selectedPaymentDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val methodOptions = PaymentMethodLabels.options(requireContext())
        dialogBinding.editBillPaymentMethod.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, methodOptions.map { it.first })
        )
        dialogBinding.editBillPaymentMethod.setText(methodOptions.first().first, false)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bill_payment_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_save) { _, _ ->
                val amount = dialogBinding.editBillPaymentAmount.text?.toString()?.trim()?.toLongOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(requireContext(), R.string.bill_payment_error_amount_required, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val methodLabel = dialogBinding.editBillPaymentMethod.text?.toString()
                val methodValue = methodOptions.firstOrNull { it.first == methodLabel }?.second
                    ?: "CASH"
                val note = dialogBinding.editBillPaymentNote.text?.toString()?.trim()

                viewLifecycleOwner.lifecycleScope.launch {
                    val paymentRepository = PaymentRepository(AppDatabase.getInstance(requireContext()))
                    paymentRepository.recordBillPayment(
                        billId = bill.billId,
                        supplierId = bill.supplierId,
                        amount = amount,
                        date = selectedPaymentDate,
                        paymentMethod = methodValue,
                        note = note
                    )
                    val refreshed = viewModel.loadBill(bill.billId)
                    existingBill = refreshed
                    refreshed?.let { binding.editBillPaid.setText(it.paid.toString()) }
                    Toast.makeText(requireContext(), R.string.bill_payment_saved_toast, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun setupFragmentResultListener() {
        // BillCameraFragment कडून scan result परत येतो — इथे फॉर्ममध्ये prefill
        // होतं, पण save मात्र user बटण दाबल्यावरच होतं (PART 54: mandatory review).
        setFragmentResultListener("bill_scan_result") { _, bundle ->
            val imagePath = bundle.getString("imagePath")
            val guessedTotal = bundle.getLong("guessedTotal", -1L)

            pickedImagePath = imagePath
            imagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                if (bitmap != null) {
                    binding.imageBillPreview.setImageBitmap(bitmap)
                    binding.imageBillPreview.isVisible = true
                }
            }
            if (guessedTotal > 0) {
                binding.editBillTotal.setText(guessedTotal.toString())
                Toast.makeText(requireContext(), R.string.bill_ocr_prefilled_toast, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSupplierAutocomplete() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suppliers.collect { suppliers ->
                    binding.editBillSupplier.setAdapter(
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            suppliers.map { it.name }
                        )
                    )
                }
            }
        }
    }

    private fun loadSites() {
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val siteRepository = SiteRepository(db.siteDao(), db.clientDao())
            sites = siteRepository.getSiteList().first()

            val siteNames = listOf(getString(R.string.bill_no_site)) + sites.map { it.siteName }
            binding.editBillSite.setAdapter(
                ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, siteNames)
            )
            if (binding.editBillSite.text.isNullOrBlank()) {
                binding.editBillSite.setText(getString(R.string.bill_no_site), false)
            }

            existingBill?.siteId?.let { siteId ->
                val siteName = sites.firstOrNull { it.siteId == siteId }?.siteName
                if (siteName != null) binding.editBillSite.setText(siteName, false)
            }
        }
    }

    private fun prefillFrom(bill: BillEntity) {
        selectedDate = bill.billDate
        binding.buttonPickBillDate.text = dateFormat.format(selectedDate)
        binding.editBillNumber.setText(bill.billNumber)
        binding.editBillSubtotal.setText(bill.subtotal.toString())
        binding.editBillTax.setText(bill.tax.toString())
        binding.editBillTotal.setText(bill.total.toString())
        binding.editBillPaid.setText(bill.paid.toString())
        binding.editBillNotes.setText(bill.notes)
        pickedImagePath = bill.imagePath

        bill.imagePath?.let { path ->
            val bitmap = BitmapFactory.decodeFile(path)
            if (bitmap != null) {
                binding.imageBillPreview.setImageBitmap(bitmap)
                binding.imageBillPreview.isVisible = true
            }
        }

        val supplierName = viewModel.suppliers.value.firstOrNull { it.supplierId == bill.supplierId }?.name
        binding.editBillSupplier.setText(supplierName ?: "", false)
    }

    private fun openCamera() {
        val siteName = sites.firstOrNull { it.siteName == binding.editBillSite.text?.toString() }?.siteName
        findNavController().navigate(
            R.id.billCameraFragment,
            bundleOf("siteName" to siteName)
        )
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
                binding.buttonPickBillDate.text = dateFormat.format(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun onSaveClicked(force: Boolean) {
        val total = binding.editBillTotal.text?.toString()?.trim()?.toLongOrNull()
        if (total == null || total <= 0) {
            binding.inputLayoutBillTotal.error = getString(R.string.bill_error_total_required)
            return
        }
        binding.inputLayoutBillTotal.error = null

        val supplierName = binding.editBillSupplier.text?.toString()?.trim()
        val siteName = binding.editBillSite.text?.toString()?.trim()
        val siteId = sites.firstOrNull { it.siteName == siteName }?.siteId
        val billNumber = binding.editBillNumber.text?.toString()?.trim()
        val subtotal = binding.editBillSubtotal.text?.toString()?.trim()?.toLongOrNull() ?: 0L
        val tax = binding.editBillTax.text?.toString()?.trim()?.toLongOrNull() ?: 0L
        val paid = binding.editBillPaid.text?.toString()?.trim()?.toLongOrNull() ?: 0L
        val notes = binding.editBillNotes.text?.toString()?.trim()

        viewLifecycleOwner.lifecycleScope.launch {
            if (!force && existingBill == null && !supplierName.isNullOrBlank() && !billNumber.isNullOrBlank()) {
                // PART 24 — Duplicate check फक्त नवीन bill साठी, आणि supplier आधीच
                // असेल तरच (नवीन supplier असेल तर duplicate असूच शकत नाही).
                val db = AppDatabase.getInstance(requireContext())
                val existingSupplier = db.supplierDao().findByName(supplierName.trim())
                if (existingSupplier != null) {
                    val duplicate = viewModel.checkDuplicate(
                        existingSupplier.supplierId, billNumber, selectedDate, total
                    )
                    if (duplicate != null) {
                        showDuplicateWarning()
                        return@launch
                    }
                }
            }

            viewModel.saveBill(
                existingBill = existingBill,
                supplierName = supplierName,
                siteId = siteId,
                billNumber = billNumber,
                billDate = selectedDate,
                subtotal = subtotal,
                tax = tax,
                total = total,
                paid = paid,
                imagePath = pickedImagePath,
                notes = notes,
                onSaved = {
                    Toast.makeText(requireContext(), R.string.bill_saved_toast, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                },
                onError = {
                    Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun showDuplicateWarning() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bill_duplicate_title)
            .setMessage(R.string.bill_duplicate_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_save_anyway) { _, _ -> onSaveClicked(force = true) }
            .show()
    }

    private fun confirmDelete() {
        val bill = existingBill ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.bill_delete_confirm_title)
            .setMessage(R.string.bill_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteBill(bill) {
                    Toast.makeText(requireContext(), R.string.bill_deleted_toast, Toast.LENGTH_SHORT).show()
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
