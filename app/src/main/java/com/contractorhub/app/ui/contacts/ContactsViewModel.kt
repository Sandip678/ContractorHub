package com.contractorhub.app.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ContactEntity
import com.contractorhub.app.data.repository.ContactRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContactRepository(AppDatabase.getInstance(application).contactDao())

    val contacts: StateFlow<List<ContactEntity>> = repository.getContacts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch { repository.deleteContact(contact) }
    }
}
