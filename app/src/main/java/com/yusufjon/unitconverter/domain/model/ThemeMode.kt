package com.yusufjon.unitconverter.domain.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,

    ;

    companion object {
        fun fromName(value: String?): ThemeMode {
            return entries.firstOrNull { it.name == value } ?: SYSTEM
        }
    }
}
