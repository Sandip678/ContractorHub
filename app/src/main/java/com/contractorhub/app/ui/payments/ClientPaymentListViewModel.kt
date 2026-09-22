package com.contractorhub.app.ui.payments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import com.contractorhub.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ClientPaymentListViewModel(application: Application) : AndroidViewModel(application) {

    val payments: StateFlow<List<ClientPaymentEntity>>

    init {
        val repository = PaymentRepository(AppDatabase.getInstance(application))
        payments = repository.getClientPayments()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
