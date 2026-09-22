package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PART 27 — Personal Daily Diary. बहुतांश नोंदी **automatic** असतात — Expense
 * किंवा Client Payment save झाला की इथेही आपोआप येतो (PART 7: one entry →
 * multiple updates). `sourceType`+`sourceId` मूळ नोंदीकडे परत जोडतो.
 *
 * Deterministic ID वापरतो (उदा. "expense_<expenseId>") — त्यामुळे तोच Expense
 * edit केला की insert REPLACE आपोआप जुनी diary entry अपडेट करतो, वेगळा update
 * query लागत नाही. मूळ Expense/Payment delete झाला की हीच entry सुद्धा delete
 * होते (repository मध्ये त्याच transaction मध्ये).
 *
 * `type = NOTE` असलेल्या entries फक्त user ने manually टाकलेल्या असतात
 * (sourceType/sourceId null) — त्याच फक्त थेट delete करता येतात.
 */
@Entity(tableName = "diary_transactions")
data class DiaryTransactionEntity(
    @PrimaryKey val diaryId: String,
    val date: Long,
    val type: String,
    val category: String? = null,
    val amount: Long = 0,
    val sourceType: String? = null,
    val sourceId: String? = null,
    val siteId: String? = null,
    val note: String? = null
)

enum class DiaryEntryType { INCOME, EXPENSE, NOTE }
