package com.yusufjon.unitconverter.domain.repository

import com.yusufjon.unitconverter.domain.model.FavoriteConversion
import com.yusufjon.unitconverter.domain.model.UnitCategory
import kotlinx.coroutines.flow.Flow

interface FavoriteConversionRepository {
    fun observeAll(): Flow<List<FavoriteConversion>>

    suspend fun add(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    )

    suspend fun remove(id: Long)

    suspend fun remove(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    )
}
