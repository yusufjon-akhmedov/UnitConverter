package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.repository.FavoriteConversionRepository

class RemoveFavoriteConversionUseCase(
    private val repository: FavoriteConversionRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.remove(id)
    }
}
