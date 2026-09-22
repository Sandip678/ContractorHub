package com.contractorhub.app.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.repository.ExpenseRepository
import com.contractorhub.app.data.repository.MaterialRepository
import com.contractorhub.app.data.repository.PaymentRepository
import com.contractorhub.app.data.repository.SiteRepository
import com.contractorhub.app.domain.model.DashboardSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * PART 5 — Architecture: UI → ViewModel → Repository → Room.
 *
 * Phase 3 पासून Active Sites, Phase 7-8 पासून Low Stock, आणि आता Phase 10-11
 * पासून Today's Income/Expense — सगळे खऱ्या repositories मधून येतात. उरलेला
 * फक्त "Labour Payable" (सगळ्या labours च्या balances ची बेरीज करणारी aggregate
 * query अजून लिहिलेली नाही — छोटा follow-up) आणि Site Status/Recent Transactions
 * dummy आहेत — ते Diary (Phase 12) आणि Profit/Loss (Phase 13) मध्ये जोडायचे.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    val uiState: StateFlow<DashboardSummary>

    init {
        val db = AppDatabase.getInstance(application)
        val siteRepository = SiteRepository(db.siteDao(), db.clientDao())
        val materialRepository = MaterialRepository(db)
        val expenseRepository = ExpenseRepository(db)
        val paymentRepository = PaymentRepository(db)
        val dummy = DummyDashboardData.get()

        uiState = combine(
            siteRepository.getActiveSiteCount(),
            materialRepository.getLowStockCount(),
            expenseRepository.getTodayTotal(),
            paymentRepository.getTodayTotal()
        ) { activeSiteCount, lowStockCount, todayExpense, todayIncome ->
            dummy.copy(
                activeSitesCount = activeSiteCount,
                lowStockCount = lowStockCount,
                todayExpense = todayExpense,
                todayIncome = todayIncome
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), dummy)
    }
}
