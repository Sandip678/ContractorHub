package com.contractorhub.app.ui.sites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.SiteEntity
import com.contractorhub.app.data.local.entity.SiteStatus
import com.contractorhub.app.databinding.FragmentAddEditSiteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * PART 15/48 — Add/Edit Site form. एकाच फॉर्मने add आणि edit दोन्ही होतात —
 * `siteId` argument null असेल तर "Add", नसेल तर "Edit" + existing data prefilled.
 *
 * Client field: टाईप केलेलं नाव आधीच database मध्ये असेल तर तोच client वापरला
 * जातो, नसेल तर नवीन तयार होतो (SiteRepository.saveSiteWithClientName) — यामुळे
 * duplicate client entries तयार होत नाहीत (roadmap चं core तत्त्व).
 */
class AddEditSiteFragment : Fragment() {

    private var _binding: FragmentAddEditSiteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditSiteViewModel by viewModels()

    private var siteId: String? = null
    private var existingSite: SiteEntity? = null

    private val statusOptions by lazy {
        listOf(
            getString(R.string.site_status_active) to SiteStatus.ACTIVE.name,
            getString(R.string.site_status_on_hold) to SiteStatus.ON_HOLD.name,
            getString(R.string.site_status_completed) to SiteStatus.COMPLETED.name
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditSiteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        siteId = arguments?.getString("siteId")

        setupStatusDropdown()
        setupClientAutocomplete()

        binding.editStatus.setText(statusOptions.first().first, false)

        if (siteId != null) {
            requireActivity().title = getString(R.string.site_edit_title)
            binding.buttonDeleteSite.visibility = View.VISIBLE
            loadExistingSite(siteId!!)
        } else {
            requireActivity().title = getString(R.string.site_add_title)
        }

        binding.buttonSaveSite.setOnClickListener { onSaveClicked() }
        binding.buttonDeleteSite.setOnClickListener { confirmDelete() }
    }

    private fun setupStatusDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            statusOptions.map { it.first }
        )
        binding.editStatus.setAdapter(adapter)
    }

    private fun setupClientAutocomplete() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.clients.collect { clients ->
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        clients.map { it.name }
                    )
                    binding.editClientName.setAdapter(adapter)
                }
            }
        }
    }

    private fun loadExistingSite(id: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val site = viewModel.loadSite(id)
            existingSite = site
            site ?: return@launch

            binding.editSiteName.setText(site.siteName)
            binding.editAddress.setText(site.address)
            binding.editContractAmount.setText(site.contractAmount.toString())
            binding.editNotes.setText(site.notes)

            val statusLabel = statusOptions.firstOrNull { it.second == site.status }?.first
                ?: statusOptions.first().first
            binding.editStatus.setText(statusLabel, false)

            site.clientId?.let { clientId ->
                val client = viewModel.clients.value.firstOrNull { it.clientId == clientId }
                binding.editClientName.setText(client?.name ?: "")
            }
        }
    }

    private fun onSaveClicked() {
        val name = binding.editSiteName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputLayoutSiteName.error = getString(R.string.site_error_name_required)
            return
        }
        binding.inputLayoutSiteName.error = null

        val clientName = binding.editClientName.text?.toString()?.trim()
        val address = binding.editAddress.text?.toString()?.trim()
        val notes = binding.editNotes.text?.toString()?.trim()
        val contractAmount = binding.editContractAmount.text?.toString()?.trim()
            ?.toLongOrNull() ?: 0L

        val selectedStatusLabel = binding.editStatus.text?.toString()
        val statusValue = statusOptions.firstOrNull { it.first == selectedStatusLabel }?.second
            ?: SiteStatus.ACTIVE.name

        viewModel.saveSite(
            existingSite = existingSite,
            siteName = name,
            clientName = clientName,
            address = address,
            contractAmount = contractAmount,
            status = statusValue,
            notes = notes,
            onSaved = {
                Toast.makeText(requireContext(), R.string.site_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                // PART 83 — Error handling: existing data untouched, user ला कळवायचं.
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val site = existingSite ?: return
        // PART 48 Rule 5 / PART 84 — financial delete साठी stronger confirmation.
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.site_delete_confirm_title)
            .setMessage(R.string.site_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteSite(site) {
                    Toast.makeText(requireContext(), R.string.site_deleted_toast, Toast.LENGTH_SHORT).show()
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
