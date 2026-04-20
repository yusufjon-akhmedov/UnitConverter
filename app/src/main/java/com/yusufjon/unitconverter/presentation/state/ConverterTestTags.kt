package com.yusufjon.unitconverter.presentation.state

import com.yusufjon.unitconverter.domain.model.UnitCategory

object ConverterTestTags {
    const val INPUT_FIELD = "converter_input_field"
    const val FROM_UNIT_BUTTON = "converter_from_unit_button"
    const val TO_UNIT_BUTTON = "converter_to_unit_button"
    const val SWAP_BUTTON = "converter_swap_button"
    const val CLEAR_BUTTON = "converter_clear_button"
    const val FAVORITE_BUTTON = "converter_favorite_button"
    const val RESULT_VALUE = "converter_result_value"
    const val UNIT_PICKER_SEARCH = "converter_unit_picker_search"
    const val HISTORY_SECTION = "converter_history_section"
    const val FAVORITES_SECTION = "converter_favorites_section"

    fun categoryChip(category: UnitCategory): String = "category_${category.name}"

    fun unitOption(unitId: String): String = "unit_option_$unitId"
}
