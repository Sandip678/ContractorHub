package com.contractorhub.app.ui.materials

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
import com.contractorhub.app.data.local.entity.MaterialCategory
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.data.local.entity.MaterialUnit
import com.contractorhub.app.databinding.FragmentAddEditMaterialBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddEditMaterialFragment : Fragment() {

    private var _binding: FragmentAddEditMaterialBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditMaterialViewModel by viewModels()
    private var materialId: String? = null
    private var existingMaterial: MaterialEntity? = null

    private val categoryOptions by lazy { MaterialCategoryLabels.options(requireContext()) }
    private val unitOptions by lazy { MaterialUnitLabels.options(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditMaterialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        materialId = arguments?.getString("materialId")

        binding.editMaterialCategory.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categoryOptions.map { it.first })
        )
        binding.editMaterialUnit.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, unitOptions.map { it.first })
        )
        binding.editMaterialCategory.setText(categoryOptions.first().first, false)
        binding.editMaterialUnit.setText(unitOptions.first().first, false)

        if (materialId != null) {
            binding.buttonDeleteMaterial.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val material = viewModel.loadMaterial(materialId!!)
                existingMaterial = material
                material ?: return@launch

                binding.editMaterialName.setText(material.name)
                binding.editMinimumStock.setText(material.minimumStock.toString())
                binding.editMaterialRate.setText(material.rate.toString())

                val categoryLabel = categoryOptions.firstOrNull { it.second == material.category }?.first
                    ?: categoryOptions.first().first
                binding.editMaterialCategory.setText(categoryLabel, false)

                val unitLabel = unitOptions.firstOrNull { it.second == material.unit }?.first
                    ?: unitOptions.first().first
                binding.editMaterialUnit.setText(unitLabel, false)
            }
        }

        binding.buttonSaveMaterial.setOnClickListener { onSaveClicked() }
        binding.buttonDeleteMaterial.setOnClickListener { confirmDelete() }
    }

    private fun onSaveClicked() {
        val name = binding.editMaterialName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputLayoutMaterialName.error = getString(R.string.material_error_name_required)
            return
        }
        binding.inputLayoutMaterialName.error = null

        val categoryLabel = binding.editMaterialCategory.text?.toString()
        val categoryValue = categoryOptions.firstOrNull { it.first == categoryLabel }?.second
            ?: MaterialCategory.CUSTOM.name

        val unitLabel = binding.editMaterialUnit.text?.toString()
        val unitValue = unitOptions.firstOrNull { it.first == unitLabel }?.second
            ?: MaterialUnit.CUSTOM.name

        val minimumStock = binding.editMinimumStock.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0
        val rate = binding.editMaterialRate.text?.toString()?.trim()?.toLongOrNull() ?: 0L

        viewModel.saveMaterial(
            existingMaterial = existingMaterial,
            name = name,
            category = categoryValue,
            unit = unitValue,
            minimumStock = minimumStock,
            rate = rate,
            onSaved = {
                Toast.makeText(requireContext(), R.string.material_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val material = existingMaterial ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.material_delete_confirm_title)
            .setMessage(R.string.material_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteMaterial(material) {
                    Toast.makeText(requireContext(), R.string.material_deleted_toast, Toast.LENGTH_SHORT).show()
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
