package com.contractorhub.app.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.databinding.FragmentContactsBinding
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactsViewModel by viewModels()
    private lateinit var adapter: ContactListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactListAdapter { contact ->
            findNavController().navigate(
                R.id.addEditContactFragment,
                bundleOf("contactId" to contact.contactId)
            )
        }
        binding.recyclerContacts.adapter = adapter

        binding.fabAddContact.setOnClickListener { openAddContact() }
        binding.buttonEmptyAddContact.setOnClickListener { openAddContact() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contacts.collect { list ->
                    adapter.submitList(list)
                    binding.layoutEmptyState.isVisible = list.isEmpty()
                    binding.recyclerContacts.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    private fun openAddContact() {
        findNavController().navigate(
            R.id.addEditContactFragment,
            bundleOf("contactId" to null as String?)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerContacts.adapter = null
        _binding = null
    }
}
