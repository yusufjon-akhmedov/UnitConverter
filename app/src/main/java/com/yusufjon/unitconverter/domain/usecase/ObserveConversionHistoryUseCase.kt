package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.repository.ConversionHistoryRepository

class ObserveConversionHistoryUseCase(
    private val repository: ConversionHistoryRepository,
) {
    operator fun invoke(limit: Int = ConversionHistoryRepository.DEFAULT_HISTORY_LIMIT) =
        repository.observeRecent(limit)
}
