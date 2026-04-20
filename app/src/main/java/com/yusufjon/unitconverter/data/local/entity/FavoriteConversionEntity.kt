package com.yusufjon.unitconverter.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_conversions",
    indices = [
        Index(
            value = ["categoryName", "fromUnitId", "toUnitId"],
            unique = true,
        ),
    ],
)
data class FavoriteConversionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryName: String,
    val fromUnitId: String,
    val toUnitId: String,
    val createdAtMillis: Long,
)
