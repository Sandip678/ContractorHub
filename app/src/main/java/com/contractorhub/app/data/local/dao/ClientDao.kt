package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.contractorhub.app.data.local.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: ClientEntity)

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)

    @Query("SELECT * FROM clients ORDER BY name ASC")
    fun getAll(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE clientId = :clientId LIMIT 1")
    suspend fun getById(clientId: String): ClientEntity?

    @Query("SELECT * FROM clients WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByName(name: String): ClientEntity?
}
