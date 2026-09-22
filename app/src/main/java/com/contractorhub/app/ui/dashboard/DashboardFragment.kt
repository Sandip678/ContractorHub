package com.contractorhub.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.contractorhub.app.databinding.FragmentDashboardBinding
import com.contractorhub.app.domain.model.DashboardSummary
import com.contractorhub.app.utils.CurrencyFormatter
import kotlinx.coroutines.launch

/**
 * Home tab — PART 14 (Screen 3) + PART 81 (visual structure).
 *
 * Phase 2: [DashboardViewModel] कडून dummy [DashboardSummary] घेऊन सगळे cards,
 * Quick Actions, Site Status आणि Recent Transactions bind करतो. Quick Actions
 * अजून फक्त "coming soon" Toast दाखवतात — Phase 3+ मध्ये संबंधित module तयार
 * झाल्यावर इथे actual navigation जोडायचं.
 */
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()

    private val transactionAdapter = TransactionAdapter()
    private val siteStatusAdapter = SiteStatusAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerTransactions.adapter = transactionAdapter
        binding.recyclerSiteStatus.adapter = siteStatusAdapter
        binding.recyclerQuickActions.adapter = QuickActionAdapter(quickActions()) { action ->
            // Labour module तयार झाल्यामुळे (Phase 5-6) इथून थेट Labour List उघडतो.
            // बाकीचे quick actions संबंधित module तयार झाल्यावर असंच जोडायचं.
            if (action.id == "labour") {
                findNavController().navigate(R.id.labourListFragment)
                return@QuickActionAdapter
            }
            if (action.id == "material") {
                findNavController().navigate(R.id.materialListFragment)
                return@QuickActionAdapter
            }
            if (action.id == "scan_bill") {
                findNavController().navigate(
                    R.id.addEditBillFragment,
                    bundleOf("billId" to null as String?)
                )
                return@QuickActionAdapter
            }
            if (action.id == "expense") {
                findNavController().navigate(
                    R.id.addEditExpenseFragment,
                    bundleOf("expenseId" to null as String?)
                )
                return@QuickActionAdapter
            }
            if (action.id == "payment") {
                findNavController().navigate(
                    R.id.addEditClientPaymentFragment,
                    bundleOf("paymentId" to null as String?)
                )
                return@QuickActionAdapter
            }
            Toast.makeText(
                requireContext(),
                getString(R.string.quick_action_coming_soon_format, action.label),
                Toast.LENGTH_SHORT
            ).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { summary -> render(summary) }
            }
        }
    }

    private fun quickActions(): List<QuickAction> = listOf(
        QuickAction("site", getString(R.string.quick_action_site), R.drawable.ic_nav_sites),
        QuickAction("expense", getString(R.string.quick_action_expense), R.drawable.ic_trending_down),
        QuickAction("payment", getString(R.string.quick_action_payment), R.drawable.ic_nav_transactions),
        QuickAction("labour", getString(R.string.quick_action_labour), R.drawable.ic_add),
        QuickAction("material", getString(R.string.quick_action_material), R.drawable.ic_add),
        QuickAction("scan_bill", getString(R.string.quick_action_scan_bill), R.drawable.ic_scan)
    )

    private fun render(summary: DashboardSummary) {
        binding.textGreeting.text = getString(R.string.dashboard_greeting_morning)
        binding.textOwnerName.text = summary.ownerName

        binding.textActiveSitesCount.text = summary.activeSitesCount.toString().padStart(2, '0')
        binding.textTodayIncome.text = CurrencyFormatter.format(summary.todayIncome)
        binding.textTodayExpense.text = CurrencyFormatter.format(summary.todayExpense)
        binding.textPendingReceivable.text = CurrencyFormatter.format(summary.pendingReceivable)
        binding.textLabourPayable.text = CurrencyFormatter.format(summary.labourPayable)

        binding.layoutLowStock.isVisible = summary.lowStockCount > 0
        binding.textLowStock.text =
            getString(R.string.dashboard_low_stock_format, summary.lowStockCount)

        renderInsights(summary.insights)

        transactionAdapter.submitList(summary.recentTransactions)
        siteStatusAdapter.submitList(summary.siteStatuses)
    }

    private fun renderInsights(insights: List<String>) {
        binding.layoutInsights.removeAllViews()
        binding.layoutInsights.isVisible = insights.isNotEmpty()

        insights.forEach { insight ->
            val textView = TextView(requireContext()).apply {
                text = "•  $insight"
                setTextColor(requireContext().getColor(R.color.chub_text_secondary))
                textSize = 13f
                setPadding(0, 4, 0, 4)
            }
            binding.layoutInsights.addView(textView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerTransactions.adapter = null
        binding.recyclerSiteStatus.adapter = null
        binding.recyclerQuickActions.adapter = null
        _binding = null
    }
}
