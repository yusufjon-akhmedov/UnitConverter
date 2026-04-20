package com.yusufjon.unitconverter.domain.converter

sealed interface ParsedNumber {
    data object Empty : ParsedNumber

    data object Invalid : ParsedNumber

    data class Valid(
        val value: Double,
    ) : ParsedNumber
}

class NumericInputParser {
    fun parse(rawValue: String): ParsedNumber {
        val normalized = rawValue
            .trim()
            .replace('−', '-')
            .replace(',', '.')

        if (normalized.isBlank()) {
            return ParsedNumber.Empty
        }

        val parsed = normalized.toDoubleOrNull()
            ?.takeIf(Double::isFinite)
            ?: return ParsedNumber.Invalid

        return ParsedNumber.Valid(parsed)
    }
}
