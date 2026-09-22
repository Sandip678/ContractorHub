package com.contractorhub.app.ui.contacts

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
import com.contractorhub.app.data.local.entity.ContactCategory
import com.contractorhub.app.data.local.entity.ContactEntity
import com.contractorhub.app.databinding.FragmentAddEditContactBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddEditContactFragment : Fragment() {

    private var _binding: FragmentAddEditContactBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditContactViewModel by viewModels()
    private var contactId: String? = null
    private var existingContact: ContactEntity? = null

    private val categoryOptions by lazy { ContactCategoryLabels.options(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactId = arguments?.getString("contactId")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            categoryOptions.map { it.first }
        )
        binding.editContactCategory.setAdapter(adapter)
        binding.editContactCategory.setText(categoryOptions.first().first, false)

        if (contactId != null) {
            binding.buttonDeleteContact.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val contact = viewModel.loadContact(contactId!!)
                existingContact = contact
                contact ?: return@launch

                binding.editContactName.setText(contact.name)
                binding.editContactMobile.setText(contact.mobile)
                binding.editContactAlternateMobile.setText(contact.alternateMobile)
                binding.editContactCompany.setText(contact.company)
                binding.editContactAddress.setText(contact.address)
                binding.editContactNotes.setText(contact.notes)
                binding.checkboxFavorite.isChecked = contact.favorite

                val label = categoryOptions.firstOrNull { it.second == contact.category }?.first
                    ?: categoryOptions.first().first
                binding.editContactCategory.setText(label, false)
            }
        }

        binding.buttonSaveContact.setOnClickListener { onSaveClicked() }
        binding.buttonDeleteContact.setOnClickListener { confirmDelete() }
    }

    private fun onSaveClicked() {
        val name = binding.editContactName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputLayoutContactName.error = getString(R.string.contact_error_name_required)
            return
        }
        binding.inputLayoutContactName.error = null

        val selectedLabel = binding.editContactCategory.text?.toString()
        val categoryValue = categoryOptions.firstOrNull { it.first == selectedLabel }?.second
            ?: ContactCategory.OTHER.name

        viewModel.saveContact(
            existingContact = existingContact,
            name = name,
            category = categoryValue,
            mobile = binding.editContactMobile.text?.toString()?.trim(),
            alternateMobile = binding.editContactAlternateMobile.text?.toString()?.trim(),
            company = binding.editContactCompany.text?.toString()?.trim(),
            address = binding.editContactAddress.text?.toString()?.trim(),
            notes = binding.editContactNotes.text?.toString()?.trim(),
            favorite = binding.checkboxFavorite.isChecked,
            onSaved = {
                Toast.makeText(requireContext(), R.string.contact_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val contact = existingContact ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.contact_delete_confirm_title)
            .setMessage(R.string.contact_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteContact(contact) {
                    Toast.makeText(requireContext(), R.string.contact_deleted_toast, Toast.LENGTH_SHORT).show()
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
