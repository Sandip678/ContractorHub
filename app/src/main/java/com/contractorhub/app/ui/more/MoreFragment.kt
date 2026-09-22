package com.contractorhub.app.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.databinding.FragmentMoreBinding

/**
 * PART 88 — Module Map: Contacts, Reports, Settings वगैरे "More" tab मधून
 * उघडतात. Phase 4 मध्ये Clients + Contacts इथून जोडलेत; Settings अजून
 * placeholder Toast (त्याचा स्वतःचा phase पुढे — PART 47).
 */
class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            MoreMenuItem("labour", getString(R.string.labour_screen_title), R.drawable.ic_more_labour),
            MoreMenuItem("materials", getString(R.string.material_screen_title), R.drawable.ic_more_materials),
            MoreMenuItem("bills", getString(R.string.bill_screen_title), R.drawable.ic_more_bills),
            MoreMenuItem("expenses", getString(R.string.transactions_menu_expenses), R.drawable.ic_more_expenses),
            MoreMenuItem("payments", getString(R.string.transactions_menu_payments), R.drawable.ic_more_payments),
            MoreMenuItem("clients", getString(R.string.more_menu_clients), R.drawable.ic_more_clients),
            MoreMenuItem("contacts", getString(R.string.more_menu_contacts), R.drawable.ic_more_contacts),
            MoreMenuItem("settings", getString(R.string.more_menu_settings), R.drawable.ic_more_settings)
        )

        binding.recyclerMoreMenu.adapter = MoreMenuAdapter(items) { item ->
            when (item.id) {
                "labour" -> findNavController().navigate(R.id.labourListFragment)
                "materials" -> findNavController().navigate(R.id.materialListFragment)
                "bills" -> findNavController().navigate(R.id.billListFragment)
                "expenses" -> findNavController().navigate(R.id.expenseListFragment)
                "payments" -> findNavController().navigate(R.id.clientPaymentListFragment)
                "clients" -> findNavController().navigate(R.id.clientsFragment)
                "contacts" -> findNavController().navigate(R.id.contactsFragment)
                else -> Toast.makeText(
                    requireContext(),
                    getString(R.string.quick_action_coming_soon_format, item.label),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerMoreMenu.adapter = null
        _binding = null
    }
}
