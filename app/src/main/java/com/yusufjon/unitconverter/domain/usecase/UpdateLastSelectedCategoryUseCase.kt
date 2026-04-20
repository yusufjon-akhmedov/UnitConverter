package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.repository.UserPreferencesRepository

class UpdateLastSelectedCategoryUseCase(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(category: UnitCategory) {
        repository.setLastSelectedCategory(category)
    }
}
