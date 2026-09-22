package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LabourAttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: LabourAttendanceEntity)

    @Delete
    suspend fun delete(attendance: LabourAttendanceEntity)

    @Query("SELECT * FROM labour_attendance WHERE labourId = :labourId ORDER BY date DESC")
    fun getForLabour(labourId: String): Flow<List<LabourAttendanceEntity>>
}
