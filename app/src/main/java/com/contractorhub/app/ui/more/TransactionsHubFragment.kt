package com.contractorhub.app.ui.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.databinding.FragmentTransactionsHubBinding

/**
 * PART 4 (Screen 4) / PART 88 — Bottom nav चा "Transactions" tab. Phase 10-11
 * पूर्वी placeholder होता; आता Expenses आणि Client Payments कडे नेणारा hub
 * (Bills/Labour Ledger कडे shortcut सोयीसाठी — ते आधीच More मध्येही आहेत).
 */
class TransactionsHubFragment : Fragment() {

    private var _binding: FragmentTransactionsHubBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsHubBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            MoreMenuItem("expenses", getString(R.string.transactions_menu_expenses), R.drawable.ic_more_expenses),
            MoreMenuItem("payments", getString(R.string.transactions_menu_payments), R.drawable.ic_more_payments),
            MoreMenuItem("diary", getString(R.string.transactions_menu_diary), R.drawable.ic_more_diary),
            MoreMenuItem("bills", getString(R.string.transactions_menu_bills), R.drawable.ic_more_bills),
            MoreMenuItem("labour", getString(R.string.transactions_menu_labour), R.drawable.ic_more_labour)
        )

        binding.recyclerTransactionsMenu.adapter = MoreMenuAdapter(items) { item ->
            when (item.id) {
                "expenses" -> findNavController().navigate(R.id.expenseListFragment)
                "payments" -> findNavController().navigate(R.id.clientPaymentListFragment)
                "diary" -> findNavController().navigate(R.id.diaryFragment)
                "bills" -> findNavController().navigate(R.id.billListFragment)
                "labour" -> findNavController().navigate(R.id.labourListFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerTransactionsMenu.adapter = null
        _binding = null
    }
}
