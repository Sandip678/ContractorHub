package com.contractorhub.app.ui.payments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import com.contractorhub.app.data.repository.PaymentRepository
import kotlinx.coroutines.launch

class AddEditClientPaymentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PaymentRepository(AppDatabase.getInstance(application))

    suspend fun loadPayment(paymentId: String): ClientPaymentEntity? = repository.getClientPaymentById(paymentId)

    fun savePayment(
        existingPayment: ClientPaymentEntity?,
        clientName: String?,
        siteId: String?,
        amount: Long,
        date: Long,
        paymentMethod: String,
        reference: String?,
        note: String?,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val clientId = if (!clientName.isNullOrBlank()) {
                    repository.findOrCreateClient(clientName)
                } else null

                repository.saveClientPayment(
                    existingPayment, clientId, siteId, amount, date, paymentMethod, reference, note
                )
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deletePayment(payment: ClientPaymentEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteClientPayment(payment)
            onDeleted()
        }
    }
}
