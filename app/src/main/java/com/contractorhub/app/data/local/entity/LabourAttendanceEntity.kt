package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PART 17 — Automatic calculation: Attendance × Wage + OT.
 * `otHours` म्हणजे त्या दिवशीचे overtime hours (0 असू शकतं).
 */
@Entity(
    tableName = "labour_attendance",
    foreignKeys = [
        ForeignKey(
            entity = LabourEntity::class,
            parentColumns = ["labourId"],
            childColumns = ["labourId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("labourId")]
)
data class LabourAttendanceEntity(
    @PrimaryKey val attendanceId: String,
    val labourId: String,
    val siteId: String? = null,
    val date: Long,
    val status: String,
    val otHours: Double = 0.0,
    val note: String? = null
)

enum class AttendanceStatus { PRESENT, HALF_DAY, ABSENT, LEAVE }
