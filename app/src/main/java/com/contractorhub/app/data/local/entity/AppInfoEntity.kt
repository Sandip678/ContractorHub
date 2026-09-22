package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Phase 1 placeholder entity — फक्त Room database compile आणि एक real table
 * असावी यासाठी. Site, Client, Labour, Material, Bill, Expense... इत्यादी
 * खरे entities संबंधित phase मध्ये (PART 6.2) याच पद्धतीने जोडले जातील.
 */
@Entity(tableName = "app_info")
data class AppInfoEntity(
    @PrimaryKey val key: String,
    val value: String
)
