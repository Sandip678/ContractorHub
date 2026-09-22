package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.contractorhub.app.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: MaterialEntity)

    @Update
    suspend fun update(material: MaterialEntity)

    @Delete
    suspend fun delete(material: MaterialEntity)

    @Query("SELECT * FROM materials WHERE materialId = :materialId LIMIT 1")
    suspend fun getById(materialId: String): MaterialEntity?

    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAll(): Flow<List<MaterialEntity>>

    @Query("SELECT COUNT(*) FROM materials WHERE currentStock < minimumStock")
    fun getLowStockCount(): Flow<Int>

    @Query("UPDATE materials SET currentStock = currentStock + :delta WHERE materialId = :materialId")
    suspend fun adjustStock(materialId: String, delta: Double)
}
