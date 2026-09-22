package com.contractorhub.app.ui.labour

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.repository.LabourRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LabourListViewModel(application: Application) : AndroidViewModel(application) {

    val labourList: StateFlow<List<LabourEntity>>

    init {
        val db = AppDatabase.getInstance(application)
        val repository = LabourRepository(db.labourDao(), db.labourAttendanceDao(), db.labourTransactionDao())
        labourList = repository.getLabourList()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
