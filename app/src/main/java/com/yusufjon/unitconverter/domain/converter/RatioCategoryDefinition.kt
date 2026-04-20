package com.yusufjon.unitconverter.domain.converter

import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UnitDefinition

class RatioCategoryDefinition(
    override val category: UnitCategory,
    ratioUnits: List<RatioUnit>,
    override val defaultFromUnitId: String,
    override val defaultToUnitId: String,
) : ConversionCategoryDefinition {

    override val units: List<UnitDefinition> = ratioUnits.map(RatioUnit::definition)

    private val factorsToBase: Map<String, Double> = ratioUnits.associate { unit ->
        unit.definition.id to unit.toBaseFactor
    }

    override fun convert(
        value: Double,
        fromUnitId: String,
        toUnitId: String,
    ): Double {
        val fromFactor = factorsToBase.getValue(fromUnitId)
        val toFactor = factorsToBase.getValue(toUnitId)
        return value * fromFactor / toFactor
    }
}

data class RatioUnit(
    val definition: UnitDefinition,
    val toBaseFactor: Double,
)
