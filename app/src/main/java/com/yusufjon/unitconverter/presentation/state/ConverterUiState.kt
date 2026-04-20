package com.yusufjon.unitconverter.presentation.state

import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UnitDefinition

data class ConverterUiState(
    val isLoading: Boolean = true,
    val categories: List<UnitCategory> = UnitCategory.entries,
    val selectedCategory: UnitCategory = UnitCategory.LENGTH,
    val inputValue: String = "",
    val fromUnit: UnitDefinition = PLACEHOLDER_UNIT,
    val toUnit: UnitDefinition = PLACEHOLDER_UNIT,
    val resultState: ConverterResultUiState = ConverterResultUiState.Empty,
    val favorites: List<FavoriteConversionUiModel> = emptyList(),
    val history: List<ConversionHistoryUiModel> = emptyList(),
    val isCurrentFavorite: Boolean = false,
    val activeUnitPicker: UnitPickerUiState? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
) {
    val hasData: Boolean = favorites.isNotEmpty() || history.isNotEmpty()

    companion object {
        val PLACEHOLDER_UNIT = UnitDefinition(
            id = "placeholder",
            displayName = "Select a unit",
            symbol = "--",
        )
    }
}

data class FavoriteConversionUiModel(
    val id: Long,
    val category: UnitCategory,
    val fromUnit: UnitDefinition,
    val toUnit: UnitDefinition,
    val createdAtMillis: Long,
)

data class ConversionHistoryUiModel(
    val id: Long,
    val category: UnitCategory,
    val fromUnit: UnitDefinition,
    val toUnit: UnitDefinition,
    val inputDisplayValue: String,
    val outputDisplayValue: String,
    val createdAtMillis: Long,
)

data class UnitPickerUiState(
    val target: UnitPickerTarget,
    val title: String,
    val units: List<UnitDefinition>,
    val selectedUnitId: String,
)
