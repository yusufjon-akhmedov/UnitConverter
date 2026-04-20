package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.repository.ConversionHistoryRepository

class ClearConversionHistoryUseCase(
    private val repository: ConversionHistoryRepository,
) {
    suspend operator fun invoke() {
        repository.clear()
    }
}
