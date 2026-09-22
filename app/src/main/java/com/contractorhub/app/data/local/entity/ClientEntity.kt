package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey val clientId: String,
    val name: String,
    val mobile: String? = null,
    val address: String? = null,
    val notes: String? = null
)
