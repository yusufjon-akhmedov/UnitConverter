package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.repository.UserPreferencesRepository

class ObserveUserPreferencesUseCase(
    private val repository: UserPreferencesRepository,
) {
    operator fun invoke() = repository.observe()
}
