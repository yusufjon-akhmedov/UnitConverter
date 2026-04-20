package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.repository.UserPreferencesRepository

class UpdateThemeModeUseCase(
    private val repository: UserPreferencesRepository,
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}
