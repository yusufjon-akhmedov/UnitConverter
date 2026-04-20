package com.yusufjon.unitconverter.domain.model

data class FavoriteConversion(
    val id: Long,
    val category: UnitCategory,
    val fromUnitId: String,
    val toUnitId: String,
    val createdAtMillis: Long,
)
