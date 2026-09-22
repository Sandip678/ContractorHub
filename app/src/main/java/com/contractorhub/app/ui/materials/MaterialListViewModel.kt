package com.contractorhub.app.ui.materials

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.data.repository.MaterialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MaterialListViewModel(application: Application) : AndroidViewModel(application) {

    val materials: StateFlow<List<MaterialEntity>>

    init {
        val repository = MaterialRepository(AppDatabase.getInstance(application))
        materials = repository.getMaterials()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
