package com.yusufjon.unitconverter.domain

import com.yusufjon.unitconverter.domain.converter.NumericInputParser
import com.yusufjon.unitconverter.domain.converter.UnitCatalog
import com.yusufjon.unitconverter.domain.converter.ValueFormatter
import com.yusufjon.unitconverter.domain.model.ConversionOutcome
import com.yusufjon.unitconverter.domain.model.ConversionRequest
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.usecase.ConvertValueUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertValueUseCaseTest {
    private val useCase = ConvertValueUseCase(
        unitCatalog = UnitCatalog(),
        inputParser = NumericInputParser(),
        valueFormatter = ValueFormatter(),
    )

    @Test
    fun `length conversion uses ratio based definition`() {
        val result = useCase(
            ConversionRequest(
                rawInput = "1",
                category = UnitCategory.LENGTH,
                fromUnitId = "meter",
                toUnitId = "centimeter",
            ),
        )

        val success = result as ConversionOutcome.Success
        assertEquals(100.0, success.snapshot.outputValue, 0.0)
        assertEquals("100", success.snapshot.formattedOutput)
    }

    @Test
    fun `temperature conversion uses formula and supports negative values`() {
        val result = useCase(
            ConversionRequest(
                rawInput = "-40",
                category = UnitCategory.TEMPERATURE,
                fromUnitId = "celsius",
                toUnitId = "fahrenheit",
            ),
        )

        val success = result as ConversionOutcome.Success
        assertEquals(-40.0, success.snapshot.outputValue, 0.0)
        assertEquals("-40", success.snapshot.formattedOutput)
    }

    @Test
    fun `temperature conversion to kelvin preserves decimals`() {
        val result = useCase(
            ConversionRequest(
                rawInput = "0",
                category = UnitCategory.TEMPERATURE,
                fromUnitId = "celsius",
                toUnitId = "kelvin",
            ),
        )

        val success = result as ConversionOutcome.Success
        assertEquals(273.15, success.snapshot.outputValue, 0.0)
        assertEquals("273.15", success.snapshot.formattedOutput)
    }

    @Test
    fun `invalid input returns invalid outcome`() {
        val result = useCase(
            ConversionRequest(
                rawInput = "abc",
                category = UnitCategory.LENGTH,
                fromUnitId = "meter",
                toUnitId = "foot",
            ),
        )

        assertTrue(result is ConversionOutcome.Invalid)
    }

    @Test
    fun `blank input returns empty outcome`() {
        val result = useCase(
            ConversionRequest(
                rawInput = "",
                category = UnitCategory.TIME,
                fromUnitId = "hour",
                toUnitId = "minute",
            ),
        )

        assertTrue(result is ConversionOutcome.Empty)
    }
}
