package com.contractorhub.app.ui.expenses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ExpenseEntity
import com.contractorhub.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ExpenseListViewModel(application: Application) : AndroidViewModel(application) {

    val expenses: StateFlow<List<ExpenseEntity>>

    init {
        val repository = ExpenseRepository(AppDatabase.getInstance(application))
        expenses = repository.getExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
