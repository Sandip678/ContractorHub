package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.contractorhub.app.data.local.entity.SiteEntity
import com.contractorhub.app.data.local.relation.SiteListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(site: SiteEntity)

    @Update
    suspend fun update(site: SiteEntity)

    @Delete
    suspend fun delete(site: SiteEntity)

    @Query("SELECT * FROM sites WHERE siteId = :siteId LIMIT 1")
    suspend fun getById(siteId: String): SiteEntity?

    @Query(
        """
        SELECT site.siteId AS siteId,
               site.siteName AS siteName,
               client.name AS clientName,
               site.status AS status,
               site.contractAmount AS contractAmount
        FROM sites AS site
        LEFT JOIN clients AS client ON client.clientId = site.clientId
        ORDER BY site.createdAt DESC
        """
    )
    fun getSiteList(): Flow<List<SiteListItem>>

    @Query("SELECT COUNT(*) FROM sites WHERE status = 'ACTIVE'")
    fun getActiveSiteCount(): Flow<Int>
}
