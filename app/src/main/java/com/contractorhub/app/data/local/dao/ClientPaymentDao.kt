package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: ClientPaymentEntity)

    @Delete
    suspend fun delete(payment: ClientPaymentEntity)

    @Query("SELECT * FROM client_payments WHERE paymentId = :paymentId LIMIT 1")
    suspend fun getById(paymentId: String): ClientPaymentEntity?

    @Query("SELECT * FROM client_payments ORDER BY date DESC")
    fun getAll(): Flow<List<ClientPaymentEntity>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM client_payments WHERE siteId = :siteId")
    fun getTotalForSite(siteId: String): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM client_payments WHERE date BETWEEN :start AND :end")
    fun getTotalForDateRange(start: Long, end: Long): Flow<Long>
}
