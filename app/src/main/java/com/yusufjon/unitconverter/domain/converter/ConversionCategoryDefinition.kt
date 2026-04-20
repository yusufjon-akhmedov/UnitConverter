package com.yusufjon.unitconverter.domain.converter

import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UnitDefinition

sealed interface ConversionCategoryDefinition {
    val category: UnitCategory
    val units: List<UnitDefinition>
    val defaultFromUnitId: String
    val defaultToUnitId: String

    fun convert(
        value: Double,
        fromUnitId: String,
        toUnitId: String,
    ): Double

    fun unitOrThrow(unitId: String): UnitDefinition {
        return units.firstOrNull { it.id == unitId }
            ?: error("Unknown unit '$unitId' for ${category.name}")
    }

    fun inputUnitOrDefault(unitId: String): UnitDefinition {
        return units.firstOrNull { it.id == unitId } ?: unitOrThrow(defaultFromUnitId)
    }

    fun outputUnitOrDefault(unitId: String): UnitDefinition {
        return units.firstOrNull { it.id == unitId } ?: unitOrThrow(defaultToUnitId)
    }
}
