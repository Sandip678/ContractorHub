package com.contractorhub.app.data.repository

import com.contractorhub.app.data.local.dao.ClientDao
import com.contractorhub.app.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ClientRepository(private val clientDao: ClientDao) {

    fun getClients(): Flow<List<ClientEntity>> = clientDao.getAll()

    suspend fun getClientById(clientId: String): ClientEntity? = clientDao.getById(clientId)

    suspend fun saveClient(
        existingClient: ClientEntity?,
        name: String,
        mobile: String?,
        address: String?,
        notes: String?
    ) {
        val client = existingClient?.copy(
            name = name,
            mobile = mobile,
            address = address,
            notes = notes
        ) ?: ClientEntity(
            clientId = UUID.randomUUID().toString(),
            name = name,
            mobile = mobile,
            address = address,
            notes = notes
        )
        clientDao.insert(client) // REPLACE strategy handles both insert + update by PK
    }

    suspend fun deleteClient(client: ClientEntity) = clientDao.delete(client)
}
