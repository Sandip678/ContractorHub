package com.contractorhub.app.ui.contacts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.ContactEntity
import com.contractorhub.app.data.repository.ContactRepository
import kotlinx.coroutines.launch

class AddEditContactViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContactRepository(AppDatabase.getInstance(application).contactDao())

    suspend fun loadContact(contactId: String): ContactEntity? = repository.getContactById(contactId)

    fun saveContact(
        existingContact: ContactEntity?,
        name: String,
        category: String,
        mobile: String?,
        alternateMobile: String?,
        company: String?,
        address: String?,
        notes: String?,
        favorite: Boolean,
        onSaved: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.saveContact(
                    existingContact, name, category, mobile,
                    alternateMobile, company, address, notes, favorite
                )
                onSaved()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }

    fun deleteContact(contact: ContactEntity, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteContact(contact)
            onDeleted()
        }
    }
}
