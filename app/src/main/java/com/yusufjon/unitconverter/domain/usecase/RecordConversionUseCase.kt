package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.model.ConversionSnapshot
import com.yusufjon.unitconverter.domain.repository.ConversionHistoryRepository

class RecordConversionUseCase(
    private val repository: ConversionHistoryRepository,
) {
    suspend operator fun invoke(snapshot: ConversionSnapshot) {
        repository.record(snapshot)
    }
}
