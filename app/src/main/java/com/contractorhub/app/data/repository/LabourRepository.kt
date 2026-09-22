package com.contractorhub.app.data.repository

import com.contractorhub.app.data.local.dao.LabourAttendanceDao
import com.contractorhub.app.data.local.dao.LabourDao
import com.contractorhub.app.data.local.dao.LabourTransactionDao
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 5 — Architecture: UI → ViewModel → Repository → Room.
 * Labour, Attendance आणि Transactions तिन्ही एकाच repository मधून — कारण
 * Balance (PART 16) हे तिन्ही combine करून काढावं लागतं.
 */
class LabourRepository(
    private val labourDao: LabourDao,
    private val attendanceDao: LabourAttendanceDao,
    private val transactionDao: LabourTransactionDao
) {

    fun getLabourList(): Flow<List<LabourEntity>> = labourDao.getAll()

    suspend fun getLabourById(labourId: String): LabourEntity? = labourDao.getById(labourId)

    suspend fun deleteLabour(labour: LabourEntity) = labourDao.delete(labour)

    suspend fun saveLabour(
        existingLabour: LabourEntity?,
        name: String,
        mobile: String?,
        workType: String?,
        dailyRate: Long,
        status: String,
        notes: String?
    ) {
        val labour = existingLabour?.copy(
            name = name,
            mobile = mobile,
            workType = workType,
            dailyRate = dailyRate,
            status = status,
            notes = notes
        ) ?: LabourEntity(
            labourId = UUID.randomUUID().toString(),
            name = name,
            mobile = mobile,
            workType = workType,
            dailyRate = dailyRate,
            status = status,
            notes = notes
        )
        labourDao.insert(labour)
    }

    fun getAttendance(labourId: String): Flow<List<LabourAttendanceEntity>> =
        attendanceDao.getForLabour(labourId)

    fun getTransactions(labourId: String): Flow<List<LabourTransactionEntity>> =
        transactionDao.getForLabour(labourId)

    suspend fun addAttendance(
        labourId: String,
        siteId: String?,
        date: Long,
        status: String,
        otHours: Double,
        note: String?
    ) {
        attendanceDao.insert(
            LabourAttendanceEntity(
                attendanceId = UUID.randomUUID().toString(),
                labourId = labourId,
                siteId = siteId,
                date = date,
                status = status,
                otHours = otHours,
                note = note
            )
        )
    }

    suspend fun deleteAttendance(attendance: LabourAttendanceEntity) =
        attendanceDao.delete(attendance)

    suspend fun addTransaction(
        labourId: String,
        siteId: String?,
        date: Long,
        type: String,
        amount: Long,
        note: String?
    ) {
        transactionDao.insert(
            LabourTransactionEntity(
                transactionId = UUID.randomUUID().toString(),
                labourId = labourId,
                siteId = siteId,
                date = date,
                type = type,
                amount = amount,
                note = note
            )
        )
    }

    suspend fun deleteTransaction(transaction: LabourTransactionEntity) =
        transactionDao.delete(transaction)
}
