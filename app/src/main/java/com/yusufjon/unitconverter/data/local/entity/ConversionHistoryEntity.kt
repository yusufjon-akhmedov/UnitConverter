package com.yusufjon.unitconverter.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String,
    val fromUnitId: String,
    val toUnitId: String,
    val inputValue: Double,
    val outputValue: Double,
    val createdAtMillis: Long,
)
