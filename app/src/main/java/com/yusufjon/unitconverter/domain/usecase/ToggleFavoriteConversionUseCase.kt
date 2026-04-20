package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.repository.FavoriteConversionRepository

class ToggleFavoriteConversionUseCase(
    private val repository: FavoriteConversionRepository,
) {
    suspend operator fun invoke(
        isFavorite: Boolean,
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        if (isFavorite) {
            repository.remove(
                category = category,
                fromUnitId = fromUnitId,
                toUnitId = toUnitId,
            )
        } else {
            repository.add(
                category = category,
                fromUnitId = fromUnitId,
                toUnitId = toUnitId,
            )
        }
    }
}
