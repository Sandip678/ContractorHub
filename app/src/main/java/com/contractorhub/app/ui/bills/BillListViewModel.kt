package com.contractorhub.app.ui.bills

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.BillEntity
import com.contractorhub.app.data.local.entity.SupplierEntity
import com.contractorhub.app.data.repository.BillRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class BillListViewModel(application: Application) : AndroidViewModel(application) {

    val bills: StateFlow<List<BillEntity>>
    val suppliers: StateFlow<List<SupplierEntity>>

    init {
        val repository = BillRepository(AppDatabase.getInstance(application))
        bills = repository.getBills()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        suppliers = repository.getSuppliers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
