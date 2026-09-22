package com.contractorhub.app.ui.materials

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
import com.contractorhub.app.data.local.entity.MaterialStockTransactionEntity
import com.contractorhub.app.data.local.entity.StockTransactionType
import com.contractorhub.app.databinding.DialogAddStockTransactionBinding
import com.contractorhub.app.databinding.FragmentMaterialProfileBinding
import com.contractorhub.app.utils.CurrencyFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MaterialProfileFragment : Fragment() {

    private var _binding: FragmentMaterialProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MaterialProfileViewModel by viewModels()
    private lateinit var adapter: StockTransactionAdapter

    private var materialId: String = ""
    private val qtyFormat = DecimalFormat("#,##0.##")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMaterialProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        materialId = arguments?.getString("materialId").orEmpty()
        viewModel.setMaterialId(materialId)

        adapter = StockTransactionAdapter(unit = "") { transaction -> confirmDeleteStock(transaction) }
        binding.recyclerStockHistory.adapter = adapter

        binding.imageEditMaterial.setOnClickListener {
            findNavController().navigate(
                R.id.addEditMaterialFragment,
                bundleOf("materialId" to materialId)
            )
        }
        binding.buttonAddStockTransaction.setOnClickListener { showAddStockDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val material = state.material ?: return@collect
                    val context = requireContext()
                    val unitLabel = MaterialUnitLabels.label(context, material.unit)

                    binding.textMaterialProfileName.text = material.name
                    binding.textMaterialProfileCategory.text =
                        "${MaterialCategoryLabels.label(context, material.category)} • " +
                        "${CurrencyFormatter.format(material.rate)}/$unitLabel"

                    binding.textCurrentStock.text = "${qtyFormat.format(material.currentStock)} $unitLabel"
                    binding.textMinimumStock.text = getString(
                        R.string.material_minimum_stock_format,
                        "${qtyFormat.format(material.minimumStock)} $unitLabel"
                    )
                    binding.textLowStockWarning.isVisible = material.currentStock < material.minimumStock

                    adapter = StockTransactionAdapter(unit = unitLabel) { transaction ->
                        confirmDeleteStock(transaction)
                    }
                    adapter.submitList(state.transactions)
                    binding.recyclerStockHistory.adapter = adapter
                    binding.textStockHistoryEmpty.isVisible = state.transactions.isEmpty()
                }
            }
        }
    }

    private fun showAddStockDialog() {
        val material = viewModel.uiState.value.material ?: return
        val dialogBinding = DialogAddStockTransactionBinding.inflate(layoutInflater)
        var selectedDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dialogBinding.buttonPickStockDate.text = dateFormat.format(selectedDate)

        dialogBinding.buttonPickStockDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val picked = Calendar.getInstance().apply {
                        set(year, month, day, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    selectedDate = picked.timeInMillis
                    dialogBinding.buttonPickStockDate.text = dateFormat.format(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val typeOptions = StockTransactionTypeLabels.options(requireContext())
        dialogBinding.editStockType.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, typeOptions.map { it.first })
        )
        dialogBinding.editStockType.setText(typeOptions.first().first, false)

        val suppliers = viewModel.suppliers.value.map { it.name }
        dialogBinding.editSupplierName.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, suppliers)
        )

        // प्रत्येक type नुसार वेगळे fields दाखवायचे/लपवायचे (PART 19: Purchase ला
        // supplier+rate लागतो, Adjustment ला increase/decrease toggle लागतो).
        fun updateVisibility(typeValue: String) {
            dialogBinding.toggleAdjustmentDirection.isVisible =
                typeValue == StockTransactionType.ADJUSTMENT.name
            dialogBinding.inputLayoutSupplierName.isVisible =
                typeValue == StockTransactionType.PURCHASE.name
            dialogBinding.inputLayoutStockRate.isVisible =
                typeValue == StockTransactionType.PURCHASE.name
        }
        updateVisibility(typeOptions.first().second)
        dialogBinding.editStockType.setOnItemClickListener { _, _, position, _ ->
            updateVisibility(typeOptions[position].second)
        }
        dialogBinding.buttonIncrease.isChecked = true

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.stock_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_save) { _, _ ->
                val quantity = dialogBinding.editStockQuantity.text?.toString()?.trim()?.toDoubleOrNull()
                if (quantity == null || quantity <= 0) {
                    Toast.makeText(requireContext(), R.string.stock_error_quantity_required, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val typeLabel = dialogBinding.editStockType.text?.toString()
                val typeValue = typeOptions.firstOrNull { it.first == typeLabel }?.second
                    ?: StockTransactionType.PURCHASE.name
                val isIncrease = dialogBinding.buttonIncrease.isChecked
                val supplierName = dialogBinding.editSupplierName.text?.toString()?.trim()
                val rate = dialogBinding.editStockRate.text?.toString()?.trim()?.toLongOrNull()
                val note = dialogBinding.editStockNote.text?.toString()?.trim()

                viewModel.addStockTransaction(
                    siteId = null,
                    supplierName = supplierName,
                    type = typeValue,
                    quantity = quantity,
                    isIncrease = isIncrease,
                    rate = rate,
                    date = selectedDate,
                    note = note
                )
                Toast.makeText(requireContext(), R.string.stock_saved_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmDeleteStock(transaction: MaterialStockTransactionEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.stock_delete_confirm_title)
            .setMessage(R.string.stock_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteStockTransaction(transaction)
                Toast.makeText(requireContext(), R.string.stock_deleted_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerStockHistory.adapter = null
        _binding = null
    }
}
