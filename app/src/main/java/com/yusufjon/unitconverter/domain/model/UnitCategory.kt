package com.yusufjon.unitconverter.domain.model

enum class UnitCategory(
    val displayName: String,
    val description: String,
) {
    LENGTH(
        displayName = "Length",
        description = "Distance, dimensions, and everyday measurements.",
    ),
    MASS(
        displayName = "Mass",
        description = "Weight conversions for cooking, travel, and logistics.",
    ),
    TEMPERATURE(
        displayName = "Temperature",
        description = "Formula-based conversions between Celsius, Fahrenheit, and Kelvin.",
    ),
    VOLUME(
        displayName = "Volume",
        description = "Capacity measurements for recipes and liquid quantities.",
    ),
    AREA(
        displayName = "Area",
        description = "Surface measurements for spaces, land, and property.",
    ),
    SPEED(
        displayName = "Speed",
        description = "Motion and travel conversions from daily commutes to navigation.",
    ),
    TIME(
        displayName = "Time",
        description = "Durations from seconds to weeks.",
    ),
    ;

    companion object {
        fun fromName(value: String?): UnitCategory {
            return entries.firstOrNull { it.name == value } ?: LENGTH
        }
    }
}
