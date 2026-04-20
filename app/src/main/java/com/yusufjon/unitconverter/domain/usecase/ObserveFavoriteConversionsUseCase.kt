package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.repository.FavoriteConversionRepository

class ObserveFavoriteConversionsUseCase(
    private val repository: FavoriteConversionRepository,
) {
    operator fun invoke() = repository.observeAll()
}
