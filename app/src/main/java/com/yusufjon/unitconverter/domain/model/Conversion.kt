package com.yusufjon.unitconverter.domain.model

data class ConversionRequest(
    val rawInput: String,
    val category: UnitCategory,
    val fromUnitId: String,
    val toUnitId: String,
)

data class ConversionSnapshot(
    val category: UnitCategory,
    val inputValue: Double,
    val outputValue: Double,
    val formattedInput: String,
    val formattedOutput: String,
    val fromUnit: UnitDefinition,
    val toUnit: UnitDefinition,
) {
    val signature: String
        get() = buildString {
            append(category.name)
            append('|')
            append(fromUnit.id)
            append('|')
            append(toUnit.id)
            append('|')
            append(inputValue)
            append('|')
            append(outputValue)
        }
}

sealed interface ConversionOutcome {
    data object Empty : ConversionOutcome

    data class Invalid(
        val message: String,
    ) : ConversionOutcome

    data class Success(
        val snapshot: ConversionSnapshot,
    ) : ConversionOutcome
}
