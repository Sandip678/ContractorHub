package com.contractorhub.app.data.repository

import com.contractorhub.app.data.local.dao.ContactDao
import com.contractorhub.app.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ContactRepository(private val contactDao: ContactDao) {

    fun getContacts(): Flow<List<ContactEntity>> = contactDao.getAll()

    suspend fun getContactById(contactId: String): ContactEntity? = contactDao.getById(contactId)

    suspend fun deleteContact(contact: ContactEntity) = contactDao.delete(contact)

    suspend fun saveContact(
        existingContact: ContactEntity?,
        name: String,
        category: String,
        mobile: String?,
        alternateMobile: String?,
        company: String?,
        address: String?,
        notes: String?,
        favorite: Boolean
    ) {
        val contact = existingContact?.copy(
            name = name,
            category = category,
            mobile = mobile,
            alternateMobile = alternateMobile,
            company = company,
            address = address,
            notes = notes,
            favorite = favorite
        ) ?: ContactEntity(
            contactId = UUID.randomUUID().toString(),
            name = name,
            category = category,
            mobile = mobile,
            alternateMobile = alternateMobile,
            company = company,
            address = address,
            notes = notes,
            favorite = favorite
        )
        contactDao.insert(contact)
    }
}
