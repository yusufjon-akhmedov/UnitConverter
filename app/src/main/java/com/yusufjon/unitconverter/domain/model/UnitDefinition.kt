package com.yusufjon.unitconverter.domain.model

data class UnitDefinition(
    val id: String,
    val displayName: String,
    val symbol: String,
    val aliases: List<String> = emptyList(),
) {
    val displayLabel: String = "$displayName ($symbol)"

    fun matches(query: String): Boolean {
        if (query.isBlank()) return true
        val normalizedQuery = query.trim().lowercase()
        return buildList {
            add(displayName)
            add(symbol)
            addAll(aliases)
        }.any { candidate -> candidate.lowercase().contains(normalizedQuery) }
    }
}
