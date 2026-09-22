package com.contractorhub.app.domain.model

/**
 * Dashboard साठी summary numbers (PART 14, Screen 3).
 * हे Phase 2 मध्ये DashboardViewModel कडून dummy data म्हणून येतात.
 * Phase 3+ मध्ये (Sites/Labour/Materials repositories तयार झाल्यावर)
 * हेच model खऱ्या Room queries मधून भरलं जाईल — UI ला काहीही बदलावं लागणार नाही.
 */
data class DashboardSummary(
    val ownerName: String,
    val activeSitesCount: Int,
    val todayIncome: Long,
    val todayExpense: Long,
    val pendingReceivable: Long,
    val labourPayable: Long,
    val lowStockCount: Int,
    val insights: List<String>,
    val recentTransactions: List<TransactionItem>,
    val siteStatuses: List<SiteStatusItem>
)

enum class TransactionType { INCOME, EXPENSE }

data class TransactionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Long,
    val type: TransactionType
)

data class SiteStatusItem(
    val id: String,
    val siteName: String,
    val status: String,
    val contractAmount: Long,
    val receivedAmount: Long,
    val pendingAmount: Long,
    val expense: Long,
    val profit: Long
)
