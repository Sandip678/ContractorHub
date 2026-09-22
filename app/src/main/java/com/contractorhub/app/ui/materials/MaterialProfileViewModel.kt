package com.contractorhub.app.ui.materials

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.data.local.entity.MaterialStockTransactionEntity
import com.contractorhub.app.data.local.entity.SupplierEntity
import com.contractorhub.app.data.repository.MaterialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MaterialProfileUiState(
    val material: MaterialEntity? = null,
    val transactions: List<MaterialStockTransactionEntity> = emptyList()
)

class MaterialProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MaterialRepository(AppDatabase.getInstance(application))
    private val materialIdFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MaterialProfileUiState>
    val suppliers: StateFlow<List<SupplierEntity>> = repository.getSuppliers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        uiState = flow {
            materialIdFlow.collect { id ->
                if (id == null) {
                    emit(MaterialProfileUiState())
                } else {
                    repository.getStockTransactions(id).collect { transactions ->
                        val material = repository.getMaterialById(id)
                        emit(MaterialProfileUiState(material = material, transactions = transactions))
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MaterialProfileUiState())
    }

    fun setMaterialId(materialId: String) {
        materialIdFlow.value = materialId
    }

    fun addStockTransaction(
        siteId: String?,
        supplierName: String?,
        type: String,
        quantity: Double,
        isIncrease: Boolean,
        rate: Long?,
        date: Long,
        note: String?
    ) {
        val id = materialIdFlow.value ?: return
        viewModelScope.launch {
            val supplierId = if (!supplierName.isNullOrBlank()) {
                repository.findOrCreateSupplier(supplierName)
            } else null

            repository.recordStockTransaction(
                materialId = id,
                siteId = siteId,
                supplierId = supplierId,
                type = type,
                quantity = quantity,
                isIncrease = isIncrease,
                rate = rate,
                date = date,
                note = note
            )
        }
    }

    fun deleteStockTransaction(transaction: MaterialStockTransactionEntity) {
        viewModelScope.launch { repository.deleteStockTransaction(transaction) }
    }
}
