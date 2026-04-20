package com.yusufjon.unitconverter.domain.usecase

import com.yusufjon.unitconverter.domain.converter.NumericInputParser
import com.yusufjon.unitconverter.domain.converter.ParsedNumber
import com.yusufjon.unitconverter.domain.converter.UnitCatalog
import com.yusufjon.unitconverter.domain.converter.ValueFormatter
import com.yusufjon.unitconverter.domain.model.ConversionOutcome
import com.yusufjon.unitconverter.domain.model.ConversionRequest
import com.yusufjon.unitconverter.domain.model.ConversionSnapshot

class ConvertValueUseCase(
    private val unitCatalog: UnitCatalog,
    private val inputParser: NumericInputParser,
    private val valueFormatter: ValueFormatter,
) {
    operator fun invoke(request: ConversionRequest): ConversionOutcome {
        val definition = unitCatalog.definitionFor(request.category)
        val fromUnit = definition.inputUnitOrDefault(request.fromUnitId)
        val toUnit = definition.outputUnitOrDefault(request.toUnitId)

        return when (val parsedNumber = inputParser.parse(request.rawInput)) {
            ParsedNumber.Empty -> ConversionOutcome.Empty
            ParsedNumber.Invalid -> ConversionOutcome.Invalid(
                message = "Enter a valid number to convert.",
            )
            is ParsedNumber.Valid -> {
                val outputValue = definition.convert(
                    value = parsedNumber.value,
                    fromUnitId = fromUnit.id,
                    toUnitId = toUnit.id,
                )

                ConversionOutcome.Success(
                    snapshot = ConversionSnapshot(
                        category = request.category,
                        inputValue = parsedNumber.value,
                        outputValue = outputValue,
                        formattedInput = valueFormatter.format(parsedNumber.value),
                        formattedOutput = valueFormatter.format(outputValue),
                        fromUnit = fromUnit,
                        toUnit = toUnit,
                    ),
                )
            }
        }
    }
}
