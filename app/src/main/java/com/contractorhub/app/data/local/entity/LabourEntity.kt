package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "labour")
data class LabourEntity(
    @PrimaryKey val labourId: String,
    val name: String,
    val mobile: String? = null,
    val workType: String? = null,
    val dailyRate: Long = 0,
    val joiningDate: Long? = null,
    val status: String = LabourStatus.ACTIVE.name,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class LabourStatus { ACTIVE, INACTIVE }
