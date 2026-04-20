package com.yusufjon.unitconverter.domain.converter

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

class ValueFormatter {
    fun format(value: Double): String {
        val normalized = if (abs(value) < ZERO_THRESHOLD) 0.0 else value
        val absolute = abs(normalized)

        return when {
            absolute >= SCIENTIFIC_NOTATION_UPPER_BOUND -> formatScientific(normalized)
            absolute in ZERO_THRESHOLD..<SCIENTIFIC_NOTATION_LOWER_BOUND -> formatScientific(normalized)
            else -> BigDecimal.valueOf(normalized)
                .setScale(MAX_DECIMALS, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString()
        }
    }

    private fun formatScientific(value: Double): String {
        return "%.${SCIENTIFIC_NOTATION_DECIMALS}E".format(Locale.US, value)
            .replace("E+", "e")
            .replace("E", "e")
    }

    private companion object {
        const val MAX_DECIMALS = 8
        const val SCIENTIFIC_NOTATION_DECIMALS = 4
        const val SCIENTIFIC_NOTATION_LOWER_BOUND = 0.000001
        const val SCIENTIFIC_NOTATION_UPPER_BOUND = 1_000_000_000.0
        const val ZERO_THRESHOLD = 0.0000000001
    }
}
