package com.contractorhub.app.ui.labour

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import com.contractorhub.app.data.repository.LabourRepository
import com.contractorhub.app.data.repository.SiteRepository
import com.contractorhub.app.domain.calculator.WageCalculator
import com.contractorhub.app.domain.model.LabourBalance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class LabourProfileUiState(
    val labour: LabourEntity? = null,
    val balance: LabourBalance? = null,
    val attendance: List<LabourAttendanceEntity> = emptyList(),
    val transactions: List<LabourTransactionEntity> = emptyList()
)

/**
 * PART 16 — Labour Profile. Balance इथेच combine() मध्ये live calculate होतो —
 * attendance किंवा transaction कुठूनही add/delete झाला की लगेच अपडेट होतो
 * (roadmap चं "one entry → automatic update" तत्त्व, PART 7).
 */
class LabourProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val labourRepository: LabourRepository
    private val siteRepository: SiteRepository
    private val labourIdFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LabourProfileUiState>

    init {
        val db = AppDatabase.getInstance(application)
        labourRepository = LabourRepository(db.labourDao(), db.labourAttendanceDao(), db.labourTransactionDao())
        siteRepository = SiteRepository(db.siteDao(), db.clientDao())

        uiState = labourIdFlow.let { idFlow ->
            kotlinx.coroutines.flow.flow {
                idFlow.collect { id ->
                    if (id == null) {
                        emit(LabourProfileUiState())
                    } else {
                        combine(
                            labourRepository.getAttendance(id),
                            labourRepository.getTransactions(id)
                        ) { attendance, transactions -> attendance to transactions }
                            .collect { (attendance, transactions) ->
                                val labour = labourRepository.getLabourById(id)
                                val balance = WageCalculator.calculateBalance(
                                    labour?.dailyRate ?: 0, attendance, transactions
                                )
                                emit(
                                    LabourProfileUiState(
                                        labour = labour,
                                        balance = balance,
                                        attendance = attendance,
                                        transactions = transactions
                                    )
                                )
                            }
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LabourProfileUiState())
    }

    fun setLabourId(labourId: String) {
        labourIdFlow.value = labourId
    }

    fun addAttendance(
        siteId: String?,
        date: Long,
        status: String,
        otHours: Double,
        note: String?
    ) {
        val id = labourIdFlow.value ?: return
        viewModelScope.launch {
            labourRepository.addAttendance(id, siteId, date, status, otHours, note)
        }
    }

    fun deleteAttendance(attendance: LabourAttendanceEntity) {
        viewModelScope.launch { labourRepository.deleteAttendance(attendance) }
    }

    fun addTransaction(
        siteId: String?,
        date: Long,
        type: String,
        amount: Long,
        note: String?
    ) {
        val id = labourIdFlow.value ?: return
        viewModelScope.launch {
            labourRepository.addTransaction(id, siteId, date, type, amount, note)
        }
    }

    fun deleteTransaction(transaction: LabourTransactionEntity) {
        viewModelScope.launch { labourRepository.deleteTransaction(transaction) }
    }
}
