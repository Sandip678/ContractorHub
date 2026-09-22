package com.contractorhub.app.ui.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.data.repository.ClientRepository
import kotlinx.coroutines.launch

class AddEditClientViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ClientRepository(AppDatabase.getInstance(application).clientDao())

    suspend fun loadClient(clientId: String): ClientEntity? = repository.getClientById(clientId)

    fun saveClient(
        existingClient: ClientEntity?,
        name: String,
        mobile: String?,
        address: String?,
        notes: String?,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveClient(existingClient, name, mobile, address, notes)
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteClient(client: ClientEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteClient(client)
            onDeleted()
        }
    }
}
