package com.yusufjon.unitconverter.data.repository

import com.yusufjon.unitconverter.data.local.dao.FavoriteConversionDao
import com.yusufjon.unitconverter.data.local.entity.FavoriteConversionEntity
import com.yusufjon.unitconverter.domain.model.FavoriteConversion
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.repository.FavoriteConversionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomFavoriteConversionRepository(
    private val dao: FavoriteConversionDao,
) : FavoriteConversionRepository {

    override fun observeAll(): Flow<List<FavoriteConversion>> {
        return dao.observeAll().map { entities ->
            entities.map(FavoriteConversionEntity::toDomainModel)
        }
    }

    override suspend fun add(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        dao.insert(
            FavoriteConversionEntity(
                categoryName = category.name,
                fromUnitId = fromUnitId,
                toUnitId = toUnitId,
                createdAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun remove(id: Long) {
        dao.removeById(id)
    }

    override suspend fun remove(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        dao.removeByUnits(
            categoryName = category.name,
            fromUnitId = fromUnitId,
            toUnitId = toUnitId,
        )
    }
}

private fun FavoriteConversionEntity.toDomainModel(): FavoriteConversion {
    return FavoriteConversion(
        id = id,
        category = UnitCategory.fromName(categoryName),
        fromUnitId = fromUnitId,
        toUnitId = toUnitId,
        createdAtMillis = createdAtMillis,
    )
}
