package com.contractorhub.app.ui.dashboard

import com.contractorhub.app.domain.model.DashboardSummary
import com.contractorhub.app.domain.model.SiteStatusItem
import com.contractorhub.app.domain.model.TransactionItem
import com.contractorhub.app.domain.model.TransactionType

/**
 * Phase 2 placeholder data source. खरा data Room repositories मधून (Sites →
 * Phase 3, Labour → Phase 5-6, Materials → Phase 7-8, Bills → Phase 9,
 * Expenses/Payments → Phase 10-11, Diary → Phase 12) येईल — तेव्हा हा object
 * काढून DashboardViewModel ला संबंधित Repository चा combined Flow दिला जाईल.
 * UI/ViewModel चा contract (DashboardSummary) आत्ताच तसाच राहील.
 */
object DummyDashboardData {

    fun get(): DashboardSummary = DashboardSummary(
        ownerName = "Sandip",
        activeSitesCount = 5,
        todayIncome = 25_000,
        todayExpense = 8_500,
        pendingReceivable = 2_40_000,
        labourPayable = 35_500,
        lowStockCount = 7,
        insights = listOf(
            "Cement stock is below minimum.",
            "₹50,000 pending from Client A.",
            "₹23,500 payable to labour.",
            "Site A is over estimated budget by ₹12,500.",
            "5 unpaid supplier bills."
        ),
        recentTransactions = listOf(
            TransactionItem("t1", "Client A Payment", "Patil Residence", 50_000, TransactionType.INCOME),
            TransactionItem("t2", "Cement Purchase", "Patil Residence", 12_000, TransactionType.EXPENSE),
            TransactionItem("t3", "Labour Wages", "Patil Residence", 8_000, TransactionType.EXPENSE),
            TransactionItem("t4", "Transport", "Patil Residence", 2_500, TransactionType.EXPENSE)
        ),
        siteStatuses = listOf(
            SiteStatusItem(
                id = "s1",
                siteName = "Patil Residence",
                status = "Active",
                contractAmount = 10_00_000,
                receivedAmount = 4_50_000,
                pendingAmount = 5_50_000,
                expense = 3_20_000,
                profit = 1_30_000
            )
        )
    )
}
