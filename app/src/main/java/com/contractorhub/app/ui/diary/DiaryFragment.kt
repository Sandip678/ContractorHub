package com.contractorhub.app.ui.diary

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import com.contractorhub.app.databinding.DialogAddDiaryNoteBinding
import com.contractorhub.app.databinding.FragmentDiaryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * PART 27 — Personal Daily Diary. बहुतांश entries automatic (Expense/Client
 * Payment save झाला की Repository level वरच तयार होतात) — इथे फक्त
 * दिवसागणिक गटवार दाखवायचं आणि manual note जोडायची सोय.
 */
class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryViewModel by viewModels()
    private lateinit var adapter: DiaryDayGroupAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DiaryDayGroupAdapter { entry -> confirmDeleteNote(entry) }
        binding.recyclerDiary.adapter = adapter

        binding.fabAddDiaryNote.setOnClickListener { showAddNoteDialog() }
        binding.buttonEmptyAddDiaryNote.setOnClickListener { showAddNoteDialog() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dayGroups.collect { groups ->
                    adapter.submitList(groups)
                    binding.layoutEmptyState.isVisible = groups.isEmpty()
                    binding.recyclerDiary.isVisible = groups.isNotEmpty()
                }
            }
        }
    }

    private fun showAddNoteDialog() {
        val dialogBinding = DialogAddDiaryNoteBinding.inflate(layoutInflater)
        var selectedDate = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        dialogBinding.buttonPickNoteDate.text = dateFormat.format(selectedDate)

        dialogBinding.buttonPickNoteDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val picked = Calendar.getInstance().apply {
                        set(year, month, day, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    selectedDate = picked.timeInMillis
                    dialogBinding.buttonPickNoteDate.text = dateFormat.format(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.diary_note_dialog_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_save) { _, _ ->
                val note = dialogBinding.editNoteText.text?.toString()?.trim()
                if (note.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), R.string.diary_note_error_required, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewModel.addManualNote(selectedDate, note)
                Toast.makeText(requireContext(), R.string.diary_note_saved_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun confirmDeleteNote(entry: DiaryTransactionEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.diary_note_delete_confirm_title)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteEntry(entry)
                Toast.makeText(requireContext(), R.string.diary_note_deleted_toast, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerDiary.adapter = null
        _binding = null
    }
}
