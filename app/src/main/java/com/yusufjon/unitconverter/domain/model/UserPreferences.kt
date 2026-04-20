package com.yusufjon.unitconverter.domain.model

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val lastSelectedCategory: UnitCategory = UnitCategory.LENGTH,
)
