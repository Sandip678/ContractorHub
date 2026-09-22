package com.contractorhub.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey val materialId: String,
    val name: String,
    val category: String,
    val unit: String,
    val minimumStock: Double = 0.0,
    val currentStock: Double = 0.0,
    val rate: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MaterialCategory {
    CEMENT, STEEL, SAND, AGGREGATE, BRICKS, TILES, PIPES, WIRE, PAINT, HARDWARE, CUSTOM
}

enum class MaterialUnit {
    BAG, KG, TON, PIECE, LITRE, SQFT, METER, BRASS, CUSTOM
}
