package com.yusufjon.unitconverter.presentation.state

import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.model.UnitCategory

sealed interface ConverterAction {
    data class InputChanged(
        val value: String,
    ) : ConverterAction

    data class CategorySelected(
        val category: UnitCategory,
    ) : ConverterAction

    data class OpenUnitPicker(
        val target: UnitPickerTarget,
    ) : ConverterAction

    data class UnitSelected(
        val target: UnitPickerTarget,
        val unitId: String,
    ) : ConverterAction

    data class ApplyFavorite(
        val favoriteId: Long,
    ) : ConverterAction

    data class RemoveFavorite(
        val favoriteId: Long,
    ) : ConverterAction

    data class ApplyHistoryItem(
        val historyId: Long,
    ) : ConverterAction

    data class ThemeModeSelected(
        val themeMode: ThemeMode,
    ) : ConverterAction

    data object DismissUnitPicker : ConverterAction

    data object SwapUnits : ConverterAction

    data object ClearInput : ConverterAction

    data object ToggleFavorite : ConverterAction

    data object ClearHistory : ConverterAction
}
