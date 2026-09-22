package com.contractorhub.app.data.repository

import androidx.room.withTransaction
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.DiaryEntryType
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import com.contractorhub.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 25 — Expense Module.
 * PART 7 — Automatic Accounting Flow: Expense save झाला की Diary Transaction
 * आपोआप तयार/अपडेट होतो — वेगळी नोंद करावी लागत नाही (PART 69 प्रमाणे atomic).
 */
class ExpenseRepository(private val db: AppDatabase) {

    private val expenseDao = db.expenseDao()
    private val diaryDao = db.diaryDao()

    fun getExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAll()

    fun getExpensesForSite(siteId: String): Flow<List<ExpenseEntity>> = expenseDao.getForSite(siteId)

    fun getTotalForSite(siteId: String): Flow<Long> = expenseDao.getTotalForSite(siteId)

    fun getTodayTotal(): Flow<Long> {
        val (start, end) = todayRange()
        return expenseDao.getTotalForDateRange(start, end)
    }

    suspend fun getExpenseById(expenseId: String): ExpenseEntity? = expenseDao.getById(expenseId)

    suspend fun deleteExpense(expense: ExpenseEntity) {
        db.withTransaction {
            expenseDao.delete(expense)
            diaryDao.deleteById(diaryIdFor(expense.expenseId))
        }
    }

    suspend fun saveExpense(
        existingExpense: ExpenseEntity?,
        siteId: String?,
        category: String,
        amount: Long,
        date: Long,
        paymentMethod: String,
        billId: String?,
        note: String?
    ) {
        val expense = existingExpense?.copy(
            siteId = siteId,
            category = category,
            amount = amount,
            date = date,
            paymentMethod = paymentMethod,
            billId = billId,
            note = note
        ) ?: ExpenseEntity(
            expenseId = UUID.randomUUID().toString(),
            siteId = siteId,
            category = category,
            amount = amount,
            date = date,
            paymentMethod = paymentMethod,
            billId = billId,
            note = note
        )

        db.withTransaction {
            expenseDao.insert(expense)
            // Deterministic diaryId मुळे edit केला तरी हीच entry REPLACE होते.
            diaryDao.insert(
                DiaryTransactionEntity(
                    diaryId = diaryIdFor(expense.expenseId),
                    date = expense.date,
                    type = DiaryEntryType.EXPENSE.name,
                    category = expense.category,
                    amount = expense.amount,
                    sourceType = "EXPENSE",
                    sourceId = expense.expenseId,
                    siteId = expense.siteId,
                    note = expense.note
                )
            )
        }
    }

    private fun diaryIdFor(expenseId: String) = "expense_$expenseId"

    private fun todayRange(): Pair<Long, Long> {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        val end = cal.timeInMillis - 1
        return start to end
    }
}
