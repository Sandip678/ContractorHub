package com.contractorhub.app.ui.expenses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ExpenseEntity
import com.contractorhub.app.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class AddEditExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository(AppDatabase.getInstance(application))

    suspend fun loadExpense(expenseId: String): ExpenseEntity? = repository.getExpenseById(expenseId)

    fun saveExpense(
        existingExpense: ExpenseEntity?,
        siteId: String?,
        category: String,
        amount: Long,
        date: Long,
        paymentMethod: String,
        note: String?,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveExpense(existingExpense, siteId, category, amount, date, paymentMethod, null, note)
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            onDeleted()
        }
    }
}
