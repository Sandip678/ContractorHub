package com.contractorhub.app.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.databinding.FragmentAddEditClientBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AddEditClientFragment : Fragment() {

    private var _binding: FragmentAddEditClientBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditClientViewModel by viewModels()
    private var clientId: String? = null
    private var existingClient: ClientEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditClientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clientId = arguments?.getString("clientId")

        if (clientId != null) {
            binding.buttonDeleteClient.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch {
                val client = viewModel.loadClient(clientId!!)
                existingClient = client
                client ?: return@launch
                binding.editClientName.setText(client.name)
                binding.editClientMobile.setText(client.mobile)
                binding.editClientAddress.setText(client.address)
                binding.editClientNotes.setText(client.notes)
            }
        }

        binding.buttonSaveClient.setOnClickListener { onSaveClicked() }
        binding.buttonDeleteClient.setOnClickListener { confirmDelete() }
    }

    private fun onSaveClicked() {
        val name = binding.editClientName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputLayoutClientName.error = getString(R.string.client_error_name_required)
            return
        }
        binding.inputLayoutClientName.error = null

        viewModel.saveClient(
            existingClient = existingClient,
            name = name,
            mobile = binding.editClientMobile.text?.toString()?.trim(),
            address = binding.editClientAddress.text?.toString()?.trim(),
            notes = binding.editClientNotes.text?.toString()?.trim(),
            onSaved = {
                Toast.makeText(requireContext(), R.string.client_saved_toast, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            },
            onError = {
                Toast.makeText(requireContext(), R.string.error_generic, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun confirmDelete() {
        val client = existingClient ?: return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.client_delete_confirm_title)
            .setMessage(R.string.client_delete_confirm_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_delete) { _, _ ->
                viewModel.deleteClient(client) {
                    Toast.makeText(requireContext(), R.string.client_deleted_toast, Toast.LENGTH_SHORT).show()
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
