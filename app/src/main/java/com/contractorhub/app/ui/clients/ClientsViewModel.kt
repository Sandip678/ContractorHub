package com.contractorhub.app.ui.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.data.repository.ClientRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClientRepository
    val clients: StateFlow<List<ClientEntity>>

    init {
        val db = AppDatabase.getInstance(application)
        repository = ClientRepository(db.clientDao())
        clients = repository.getClients()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch { repository.deleteClient(client) }
    }
}
