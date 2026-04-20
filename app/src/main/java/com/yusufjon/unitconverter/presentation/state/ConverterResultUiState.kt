package com.yusufjon.unitconverter.presentation.state

sealed interface ConverterResultUiState {
    data object Empty : ConverterResultUiState

    data class Invalid(
        val message: String,
    ) : ConverterResultUiState

    data class Success(
        val value: String,
        val unitSymbol: String,
        val summary: String,
        val detail: String,
    ) : ConverterResultUiState
}
