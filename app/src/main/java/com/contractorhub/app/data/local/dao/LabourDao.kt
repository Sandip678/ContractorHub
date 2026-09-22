package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.contractorhub.app.data.local.entity.LabourEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabourDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(labour: LabourEntity)

    @Update
    suspend fun update(labour: LabourEntity)

    @Delete
    suspend fun delete(labour: LabourEntity)

    @Query("SELECT * FROM labour WHERE labourId = :labourId LIMIT 1")
    suspend fun getById(labourId: String): LabourEntity?

    @Query("SELECT * FROM labour ORDER BY name ASC")
    fun getAll(): Flow<List<LabourEntity>>
}
