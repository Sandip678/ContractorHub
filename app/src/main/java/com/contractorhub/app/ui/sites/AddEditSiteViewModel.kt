package com.contractorhub.app.ui.sites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.data.local.entity.SiteEntity
import com.contractorhub.app.data.repository.SiteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddEditSiteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SiteRepository

    val clients: StateFlow<List<ClientEntity>>

    init {
        val db = AppDatabase.getInstance(application)
        repository = SiteRepository(db.siteDao(), db.clientDao())
        clients = repository.getClients()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    suspend fun loadSite(siteId: String): SiteEntity? = repository.getSiteById(siteId)

    fun saveSite(
        existingSite: SiteEntity?,
        siteName: String,
        clientName: String?,
        address: String?,
        contractAmount: Long,
        status: String,
        notes: String?,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveSiteWithClientName(
                    existingSite = existingSite,
                    siteName = siteName,
                    clientName = clientName,
                    address = address,
                    contractAmount = contractAmount,
                    status = status,
                    notes = notes
                )
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteSite(site: SiteEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteSite(site)
            onDeleted()
        }
    }
}
