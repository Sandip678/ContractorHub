package com.contractorhub.app.ui.labour

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
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.local.entity.LabourStatus
import com.contractorhub.app.databinding.FragmentAddEditLabourBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddEditLabourFragment : Fragment() {

    private var _binding: FragmentAddEditLabourBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditLabourViewModel by viewModels()
    private var labourId: String? = null
    private var existingLabour: LabourEntity? = null

    private val statusOptions by lazy {
        listOf(
            getString(R.string.labour_status_active) to LabourStatus.ACTIVE.name,
            getString(R.string.labour_status_inactive) to LabourStatus.INACTIVE.name
        )
    }

    private val workTypeOptions by lazy {
        listOf(
            getString(R.string.worktype_mason),
            getString(R.string.worktype_helper),
            getString(R.string.worktype_electrician),
            getString(R.string.worktype_plumber),
            getString(R.string.worktype_other)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditLabourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labourId = arguments?.getString("labourId")

        binding.editLabourStatus.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statusOptions.map { it.first })
        )
        binding.editLabourStatus.setText(statusOptions.first().first, false)

        binding.editLabourWorktype.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, workTypeOptions)
        )

        if (labourId != null) {
            binding.buttonDeleteLabour.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val labour = viewModel.loadLabour(labourId!!)
                existingLabour = labour
                labour ?: return@launch

                binding.editLabourName.setText(labour.name)
                binding.editLabourMobile.setText(labour.mobile)
                binding.editLabourWorktype.setText(labour.workType ?: "", false)
                binding.editDailyRate.setText(labour.dailyRate.toString())
                binding.editLabourNotes.setText(labour.notes)

                val statusLabel = statusOptions.firstOrNull { it.second == labour.status }?.first
                    ?: statusOptions.first().first
                binding.editLabourStatus.setText(statusLabel, false)
            }
        }

        binding.buttonSaveLabour.setOnClickListener { onSaveClicked() }
        binding.buttonDeleteLabour.setOnClickListener { confirmDelete() }
    }

    private fun onSaveClicked() {
        val name = binding.editLabourName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputLayoutLabourName.error = getString(R.string.labour_error_name_required)
            return
        }
        binding.inputLayoutLabourName.error = null

        val dailyRate = binding.editDailyRate.text?.toString()?.trim()?.toLongOrNull() ?: 0L
        val selectedStatusLabel = binding.editLabourStatus.text?.toString()
        val statusValue = statusOptions.firstOrNull { it.first == selectedStatusLabel }?.second
            ?: LabourStatus.ACTIVE.name

        viewModel.saveLabour(
            existingLabour = existingLabour,
            name = name,
            mobile = binding.editLabourMobile.text?.toString()?.trim(),
            workType = binding.editLabourWorktype.text?.toString()?.trim(),
            dailyRate = dailyRate,
            status = statusValue,
            notes = binding.editLabourNotes.text?.toString()?.trim(),
            onSaved = {
                Toast.makeText(requireContext(), R.string.labour_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val labour = existingLabour ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.labour_delete_confirm_title)
            .setMessage(R.string.labour_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteLabour(labour) {
                    Toast.makeText(requireContext(), R.string.labour_deleted_toast, Toast.LENGTH_SHORT).show()
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
