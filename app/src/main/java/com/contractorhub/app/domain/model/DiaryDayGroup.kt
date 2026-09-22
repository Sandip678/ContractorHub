package com.contractorhub.app.domain.model

import com.contractorhub.app.data.local.entity.DiaryTransactionEntity

/**
 * PART 27 — "22 August → Income / Expenses / Summary" अशा दिवसागणिक गटासाठी.
 * हा फक्त raw entries वरून ViewModel मध्ये compute होतो — वेगळा साठवलेला नाही.
 */
data class DiaryDayGroup(
    val dayStartMillis: Long,
    val entries: List<DiaryTransactionEntity>
) {
    val income: Long get() = entries.filter { it.type == "INCOME" }.sumOf { it.amount }
    val expense: Long get() = entries.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val net: Long get() = income - expense
}
