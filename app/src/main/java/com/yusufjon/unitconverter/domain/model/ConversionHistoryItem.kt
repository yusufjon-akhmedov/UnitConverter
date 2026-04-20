package com.yusufjon.unitconverter.domain.model

data class ConversionHistoryItem(
    val id: Long,
    val category: UnitCategory,
    val fromUnitId: String,
    val toUnitId: String,
    val inputValue: Double,
    val outputValue: Double,
    val createdAtMillis: Long,
)
