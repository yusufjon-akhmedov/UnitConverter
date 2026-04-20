package com.yusufjon.unitconverter.data.repository

import com.yusufjon.unitconverter.data.local.dao.ConversionHistoryDao
import com.yusufjon.unitconverter.data.local.entity.ConversionHistoryEntity
import com.yusufjon.unitconverter.domain.model.ConversionHistoryItem
import com.yusufjon.unitconverter.domain.model.ConversionSnapshot
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.repository.ConversionHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomConversionHistoryRepository(
    private val dao: ConversionHistoryDao,
) : ConversionHistoryRepository {

    override fun observeRecent(limit: Int): Flow<List<ConversionHistoryItem>> {
        return dao.observeRecent(limit).map { entities ->
            entities.map(ConversionHistoryEntity::toDomainModel)
        }
    }

    override suspend fun record(snapshot: ConversionSnapshot) {
        dao.insert(
            ConversionHistoryEntity(
                categoryName = snapshot.category.name,
                fromUnitId = snapshot.fromUnit.id,
                toUnitId = snapshot.toUnit.id,
                inputValue = snapshot.inputValue,
                outputValue = snapshot.outputValue,
                createdAtMillis = System.currentTimeMillis(),
            ),
        )
        dao.trimTo(ConversionHistoryRepository.DEFAULT_HISTORY_LIMIT)
    }

    override suspend fun clear() {
        dao.clear()
    }
}

private fun ConversionHistoryEntity.toDomainModel(): ConversionHistoryItem {
    return ConversionHistoryItem(
        id = id,
        category = UnitCategory.fromName(categoryName),
        fromUnitId = fromUnitId,
        toUnitId = toUnitId,
        inputValue = inputValue,
        outputValue = outputValue,
        createdAtMillis = createdAtMillis,
    )
}
