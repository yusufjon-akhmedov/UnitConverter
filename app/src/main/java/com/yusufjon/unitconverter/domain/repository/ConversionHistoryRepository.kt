package com.yusufjon.unitconverter.domain.repository

import com.yusufjon.unitconverter.domain.model.ConversionHistoryItem
import com.yusufjon.unitconverter.domain.model.ConversionSnapshot
import kotlinx.coroutines.flow.Flow

interface ConversionHistoryRepository {
    fun observeRecent(limit: Int = DEFAULT_HISTORY_LIMIT): Flow<List<ConversionHistoryItem>>

    suspend fun record(snapshot: ConversionSnapshot)

    suspend fun clear()

    companion object {
        const val DEFAULT_HISTORY_LIMIT = 20
    }
}
