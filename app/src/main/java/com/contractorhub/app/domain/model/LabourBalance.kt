package com.contractorhub.app.domain.model

/**
 * PART 16 — Labour Profile "Money" section.
 * `paid` = सगळ्या ledger transactions (Advance+Angavar+Payment+Adjustment+
 * Settlement) ची बेरीज — रोडमॅपच्या उदाहरणात Advance ₹5,000 + Angavar ₹3,500 =
 * Paid ₹8,500 असंच दिसतं, म्हणजे Paid हा त्या sub-categories चा total आहे.
 * `balance` = totalEarned - paid.
 */
data class LabourBalance(
    val totalEarned: Long,
    val paid: Long,
    val advanceTotal: Long,
    val angavarTotal: Long,
    val presentDays: Int,
    val halfDays: Int,
    val otHours: Double
) {
    val balance: Long get() = totalEarned - paid
}
