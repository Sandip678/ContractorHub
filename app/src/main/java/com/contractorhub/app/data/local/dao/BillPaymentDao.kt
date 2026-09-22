package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.BillPaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillPaymentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: BillPaymentEntity)

    @Delete
    suspend fun delete(payment: BillPaymentEntity)

    @Query("SELECT * FROM bill_payments WHERE billId = :billId ORDER BY date DESC")
    fun getForBill(billId: String): Flow<List<BillPaymentEntity>>
}
