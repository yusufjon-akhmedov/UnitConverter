package com.yusufjon.unitconverter.domain.converter

import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UnitDefinition

class TemperatureCategoryDefinition : ConversionCategoryDefinition {
    override val category: UnitCategory = UnitCategory.TEMPERATURE
    override val defaultFromUnitId: String = CELSIUS.id
    override val defaultToUnitId: String = FAHRENHEIT.id
    override val units: List<UnitDefinition> = listOf(CELSIUS, FAHRENHEIT, KELVIN)

    override fun convert(
        value: Double,
        fromUnitId: String,
        toUnitId: String,
    ): Double {
        val inKelvin = when (fromUnitId) {
            CELSIUS.id -> value + 273.15
            FAHRENHEIT.id -> (value - 32.0) * (5.0 / 9.0) + 273.15
            KELVIN.id -> value
            else -> error("Unsupported temperature unit: $fromUnitId")
        }

        return when (toUnitId) {
            CELSIUS.id -> inKelvin - 273.15
            FAHRENHEIT.id -> (inKelvin - 273.15) * (9.0 / 5.0) + 32.0
            KELVIN.id -> inKelvin
            else -> error("Unsupported temperature unit: $toUnitId")
        }
    }

    private companion object {
        val CELSIUS = UnitDefinition(
            id = "celsius",
            displayName = "Celsius",
            symbol = "°C",
            aliases = listOf("centigrade"),
        )
        val FAHRENHEIT = UnitDefinition(
            id = "fahrenheit",
            displayName = "Fahrenheit",
            symbol = "°F",
        )
        val KELVIN = UnitDefinition(
            id = "kelvin",
            displayName = "Kelvin",
            symbol = "K",
        )
    }
}
