package com.contractorhub.app.ui.sites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.SiteEntity
import com.contractorhub.app.data.local.relation.SiteListItem
import com.contractorhub.app.data.repository.SiteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SitesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SiteRepository

    val sites: StateFlow<List<SiteListItem>>

    init {
        val db = AppDatabase.getInstance(application)
        repository = SiteRepository(db.siteDao(), db.clientDao())
        sites = repository.getSiteList()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun deleteSite(site: SiteListItem) {
        viewModelScope.launch {
            val fullSite = repository.getSiteById(site.siteId)
            fullSite?.let { repository.deleteSite(it) }
        }
    }

    // suspend helper so the Fragment can look up the full SiteEntity when the
    // user taps a row to edit it.
    suspend fun getSiteById(siteId: String): SiteEntity? = repository.getSiteById(siteId)
}
