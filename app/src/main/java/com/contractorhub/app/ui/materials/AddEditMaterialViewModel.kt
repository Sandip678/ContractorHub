package com.contractorhub.app.ui.materials

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.data.repository.MaterialRepository
import kotlinx.coroutines.launch

class AddEditMaterialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MaterialRepository(AppDatabase.getInstance(application))

    suspend fun loadMaterial(materialId: String): MaterialEntity? = repository.getMaterialById(materialId)

    fun saveMaterial(
        existingMaterial: MaterialEntity?,
        name: String,
        category: String,
        unit: String,
        minimumStock: Double,
        rate: Long,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveMaterial(existingMaterial, name, category, unit, minimumStock, rate)
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteMaterial(material: MaterialEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteMaterial(material)
            onDeleted()
        }
    }
}
