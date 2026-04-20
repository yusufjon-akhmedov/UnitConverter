package com.yusufjon.unitconverter.domain.converter

import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UnitDefinition

class UnitCatalog {
    private val definitions: Map<UnitCategory, ConversionCategoryDefinition> = listOf(
        lengthDefinition(),
        massDefinition(),
        TemperatureCategoryDefinition(),
        volumeDefinition(),
        areaDefinition(),
        speedDefinition(),
        timeDefinition(),
    ).associateBy(ConversionCategoryDefinition::category)

    fun categories(): List<UnitCategory> = definitions.keys.toList()

    fun definitionFor(category: UnitCategory): ConversionCategoryDefinition {
        return definitions.getValue(category)
    }

    private fun lengthDefinition(): ConversionCategoryDefinition {
        return RatioCategoryDefinition(
            category = UnitCategory.LENGTH,
            ratioUnits = listOf(
                ratioUnit("millimeter", "Millimeter", "mm", 0.001),
                ratioUnit("centimeter", "Centimeter", "cm", 0.01),
                ratioUnit("meter", "Meter", "m", 1.0),
                ratioUnit("kilometer", "Kilometer", "km", 1_000.0),
                ratioUnit("inch", "Inch", "in", 0.0254, aliases = listOf("inches")),
                ratioUnit("foot", "Foot", "ft", 0.3048, aliases = listOf("feet")),
                ratioUnit("yard", "Yard", "yd", 0.9144),
                ratioUnit("mile", "Mile", "mi", 1_609.344),
            ),
            defaultFromUnitId = "meter",
            defaultToUnitId = "foot",
        )
    }

    private fun massDefinition(): ConversionCategoryDefinition {
        return RatioCategoryDefinition(
            category = UnitCategory.MASS,
            ratioUnits = listOf(
                ratioUnit("milligram", "Milligram", "mg", 0.000001),
                ratioUnit("gram", "Gram", "g", 0.001),
                ratioUnit("kilogram", "Kilogram", "kg", 1.0),
                ratioUnit("metric_tonne", "Metric tonne", "t", 1_000.0),
                ratioUnit("ounce", "Ounce", "oz", 0.028349523125),
                ratioUnit("pound", "Pound", "lb", 0.45359237, aliases = listOf("lbs")),
                ratioUnit("stone", "Stone", "st", 6.35029318),
            ),
            defaultFromUnitId = "kilogram",
            defaultToUnitId = "pound",
        )
    }

    private fun volumeDefinition(): ConversionCategoryDefinition {
        return RatioCategoryDefinition(
            category = UnitCategory.VOLUME,
            ratioUnits = listOf(
                ratioUnit("milliliter", "Milliliter", "mL", 0.001),
                ratioUnit("liter", "Liter", "L", 1.0, aliases = listOf("litre")),
                ratioUnit("cubic_meter", "Cubic meter", "m³", 1_000.0),
                ratioUnit("teaspoon_us", "US teaspoon", "tsp", 0.00492892159375),
                ratioUnit("tablespoon_us", "US tablespoon", "tbsp", 0.01478676478125),
                ratioUnit("cup_us", "US cup", "cup", 0.2365882365),
                ratioUnit("pint_us", "US pint", "pt", 0.473176473),
                ratioUnit("gallon_us", "US gallon", "gal", 3.785411784),
            ),
            defaultFromUnitId = "liter",
            defaultToUnitId = "gallon_us",
        )
    }

    private fun areaDefinition(): ConversionCategoryDefinition {
        return RatioCategoryDefinition(
            category = UnitCategory.AREA,
            ratioUnits = listOf(
                ratioUnit("square_millimeter", "Square millimeter", "mm²", 0.000001),
                ratioUnit("square_centimeter", "Square centimeter", "cm²", 0.0001),
                ratioUnit("square_meter", "Square meter", "m²", 1.0),
                ratioUnit("hectare", "Hectare", "ha", 10_000.0),
                ratioUnit("square_kilometer", "Square kilometer", "km²", 1_000_000.0),
                ratioUnit("square_foot", "Square foot", "ft²", 0.09290304),
                ratioUnit("acre", "Acre", "ac", 4_046.8564224),
            ),
            defaultFromUnitId = "square_meter",
            defaultToUnitId = "square_foot",
        )
    }

    private fun speedDefinition(): ConversionCategoryDefinition {
        return RatioCategoryDefinition(
            category = UnitCategory.SPEED,
            ratioUnits = listOf(
                ratioUnit("meter_per_second", "Meters per second", "m/s", 1.0),
                ratioUnit("kilometer_per_hour", "Kilometers per hour", "km/h", 0.2777777777777778),
                ratioUnit("mile_per_hour", "Miles per hour", "mph", 0.44704),
                ratioUnit("knot", "Knot", "kn", 0.5144444444444445),
                ratioUnit("foot_per_second", "Feet per second", "ft/s", 0.3048),
            ),
            defaultFromUnitId = "kilometer_per_hour",
            defaultToUnitId = "mile_per_hour",
        )
    }

    private fun timeDefinition(): ConversionCategoryDefinition {
        return RatioCategoryDefinition(
            category = UnitCategory.TIME,
            ratioUnits = listOf(
                ratioUnit("second", "Second", "s", 1.0),
                ratioUnit("minute", "Minute", "min", 60.0),
                ratioUnit("hour", "Hour", "h", 3_600.0),
                ratioUnit("day", "Day", "d", 86_400.0),
                ratioUnit("week", "Week", "wk", 604_800.0),
            ),
            defaultFromUnitId = "hour",
            defaultToUnitId = "minute",
        )
    }

    private fun ratioUnit(
        id: String,
        displayName: String,
        symbol: String,
        factorToBase: Double,
        aliases: List<String> = emptyList(),
    ): RatioUnit {
        return RatioUnit(
            definition = UnitDefinition(
                id = id,
                displayName = displayName,
                symbol = symbol,
                aliases = aliases,
            ),
            toBaseFactor = factorToBase,
        )
    }
}
