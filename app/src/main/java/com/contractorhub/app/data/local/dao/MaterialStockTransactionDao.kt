package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.MaterialStockTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialStockTransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: MaterialStockTransactionEntity)

    @Delete
    suspend fun delete(transaction: MaterialStockTransactionEntity)

    @Query("SELECT * FROM material_stock_transactions WHERE materialId = :materialId ORDER BY date DESC")
    fun getForMaterial(materialId: String): Flow<List<MaterialStockTransactionEntity>>
}
