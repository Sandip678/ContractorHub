package com.contractorhub.app.data.repository

import com.contractorhub.app.data.local.dao.DiaryDao
import com.contractorhub.app.data.local.entity.DiaryEntryType
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * PART 27 — बहुतांश entries automatic (Expense/ClientPayment repositories
 * इथेच लिहितात, [ExpenseRepository]/[PaymentRepository] बघ). इथे फक्त वाचन
 * आणि manual note साठी.
 */
class DiaryRepository(private val diaryDao: DiaryDao) {

    fun getEntries(): Flow<List<DiaryTransactionEntity>> = diaryDao.getAll()

    suspend fun addManualNote(date: Long, note: String) {
        diaryDao.insert(
            DiaryTransactionEntity(
                diaryId = "note_${UUID.randomUUID()}",
                date = date,
                type = DiaryEntryType.NOTE.name,
                amount = 0,
                note = note
            )
        )
    }

    suspend fun deleteEntry(entry: DiaryTransactionEntity) = diaryDao.delete(entry)
}
