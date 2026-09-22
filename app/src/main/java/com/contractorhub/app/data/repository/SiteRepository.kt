package com.contractorhub.app.data.repository

import com.contractorhub.app.data.local.dao.ClientDao
import com.contractorhub.app.data.local.dao.SiteDao
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.data.local.entity.SiteEntity
import com.contractorhub.app.data.local.relation.SiteListItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 5 — Architecture: UI → ViewModel → Repository → Room.
 * Sites आणि Clients दोन्ही एकाच repository मधून, कारण roadmap च्या "एकदा enter
 * करा, बाकीकडे automatic" तत्त्वानुसार site वाचवताना client लगेच find/create
 * व्हावा लागतो — दोन्ही tables चा एकत्र transaction इथेच होतो.
 */
class SiteRepository(
    private val siteDao: SiteDao,
    private val clientDao: ClientDao
) {

    fun getSiteList(): Flow<List<SiteListItem>> = siteDao.getSiteList()

    fun getActiveSiteCount(): Flow<Int> = siteDao.getActiveSiteCount()

    fun getClients(): Flow<List<ClientEntity>> = clientDao.getAll()

    suspend fun getSiteById(siteId: String): SiteEntity? = siteDao.getById(siteId)

    suspend fun deleteSite(site: SiteEntity) = siteDao.delete(site)

    /**
     * Site save करताना client नाव टाईप केलं असेल तर — आधीच तसा client असेल तर
     * तोच वापरायचा (duplicate client तयार करायचा नाही), नसेल तर नवीन तयार
     * करायचा. मग site त्या clientId ला जोडून save करायचा.
     */
    suspend fun saveSiteWithClientName(
        existingSite: SiteEntity?,
        siteName: String,
        clientName: String?,
        address: String?,
        contractAmount: Long,
        status: String,
        notes: String?
    ) {
        val clientId: String? = if (clientName.isNullOrBlank()) {
            null
        } else {
            val existingClient = clientDao.findByName(clientName.trim())
            if (existingClient != null) {
                existingClient.clientId
            } else {
                val newClientId = UUID.randomUUID().toString()
                clientDao.insert(ClientEntity(clientId = newClientId, name = clientName.trim()))
                newClientId
            }
        }

        val site = existingSite?.copy(
            siteName = siteName,
            clientId = clientId,
            address = address,
            contractAmount = contractAmount,
            status = status,
            notes = notes
        ) ?: SiteEntity(
            siteId = UUID.randomUUID().toString(),
            siteName = siteName,
            clientId = clientId,
            address = address,
            contractAmount = contractAmount,
            status = status,
            notes = notes
        )

        if (existingSite != null) {
            siteDao.update(site)
        } else {
            siteDao.insert(site)
        }
    }
}
