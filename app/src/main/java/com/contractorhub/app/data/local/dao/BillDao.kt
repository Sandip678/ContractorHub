package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.BillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity)

    @Delete
    suspend fun delete(bill: BillEntity)

    @Query("SELECT * FROM bills WHERE billId = :billId LIMIT 1")
    suspend fun getById(billId: String): BillEntity?

    @Query("SELECT * FROM bills ORDER BY billDate DESC")
    fun getAll(): Flow<List<BillEntity>>

    /** PART 24 — Duplicate Bill Check: Supplier + Bill Number + Date + Amount जुळणारी नोंद. */
    @Query(
        """
        SELECT * FROM bills
        WHERE supplierId = :supplierId
          AND billNumber = :billNumber
          AND billDate = :billDate
          AND total = :total
        LIMIT 1
        """
    )
    suspend fun findPossibleDuplicate(
        supplierId: String?,
        billNumber: String?,
        billDate: Long,
        total: Long
    ): BillEntity?
}
