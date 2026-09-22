package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabourTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: LabourTransactionEntity)

    @Delete
    suspend fun delete(transaction: LabourTransactionEntity)

    @Query("SELECT * FROM labour_transactions WHERE labourId = :labourId ORDER BY date DESC")
    fun getForLabour(labourId: String): Flow<List<LabourTransactionEntity>>
}
