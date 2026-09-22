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
import kotlinx.coroutines.launch

class AddEditBillViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BillRepository(AppDatabase.getInstance(application))

    val suppliers: StateFlow<List<SupplierEntity>> = repository.getSuppliers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun loadBill(billId: String): BillEntity? = repository.getBillById(billId)

    suspend fun checkDuplicate(
        supplierId: String?,
        billNumber: String?,
        billDate: Long,
        total: Long
    ): BillEntity? = repository.findPossibleDuplicate(supplierId, billNumber, billDate, total)

    fun saveBill(
        existingBill: BillEntity?,
        supplierName: String?,
        siteId: String?,
        billNumber: String?,
        billDate: Long,
        subtotal: Long,
        tax: Long,
        total: Long,
        paid: Long,
        imagePath: String?,
        notes: String?,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val supplierId = if (!supplierName.isNullOrBlank()) {
                    repository.findOrCreateSupplier(supplierName)
                } else null

                repository.saveBill(
                    existingBill = existingBill,
                    supplierId = supplierId,
                    siteId = siteId,
                    billNumber = billNumber,
                    billDate = billDate,
                    subtotal = subtotal,
                    tax = tax,
                    total = total,
                    paid = paid,
                    imagePath = imagePath,
                    notes = notes
                )
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteBill(bill: BillEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteBill(bill)
            onDeleted()
        }
    }
}
