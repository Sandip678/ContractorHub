package com.contractorhub.app.ui.labour

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.repository.LabourRepository
import kotlinx.coroutines.launch

class AddEditLabourViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LabourRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = LabourRepository(db.labourDao(), db.labourAttendanceDao(), db.labourTransactionDao())
    }

    suspend fun loadLabour(labourId: String): LabourEntity? = repository.getLabourById(labourId)

    fun saveLabour(
        existingLabour: LabourEntity?,
        name: String,
        mobile: String?,
        workType: String?,
        dailyRate: Long,
        status: String,
        notes: String?,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveLabour(existingLabour, name, mobile, workType, dailyRate, status, notes)
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteLabour(labour: LabourEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteLabour(labour)
            onDeleted()
        }
    }
}
