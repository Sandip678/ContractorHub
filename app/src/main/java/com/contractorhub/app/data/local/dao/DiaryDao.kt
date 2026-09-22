package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryTransactionEntity)

    @Delete
    suspend fun delete(entry: DiaryTransactionEntity)

    @Query("DELETE FROM diary_transactions WHERE diaryId = :diaryId")
    suspend fun deleteById(diaryId: String)

    @Query("SELECT * FROM diary_transactions ORDER BY date DESC")
    fun getAll(): Flow<List<DiaryTransactionEntity>>
}
