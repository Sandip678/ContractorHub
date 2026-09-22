package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.SupplierEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplierDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(supplier: SupplierEntity)

    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAll(): Flow<List<SupplierEntity>>

    @Query("SELECT * FROM suppliers WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun findByName(name: String): SupplierEntity?
}
