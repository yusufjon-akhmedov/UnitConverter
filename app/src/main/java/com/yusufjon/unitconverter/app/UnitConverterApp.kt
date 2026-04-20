package com.yusufjon.unitconverter.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.yusufjon.unitconverter.presentation.screen.ConverterRoute
import com.yusufjon.unitconverter.presentation.theme.UnitConverterTheme
import com.yusufjon.unitconverter.presentation.viewmodel.ConverterViewModel

@Composable
fun UnitConverterApp(
    appContainer: AppContainer,
) {
    val dependencies = appContainer.converterViewModelDependencies
    val viewModel: ConverterViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                ConverterViewModel(
                    unitCatalog = dependencies.unitCatalog,
                    valueFormatter = dependencies.valueFormatter,
                    convertValueUseCase = dependencies.convertValueUseCase,
                    observeConversionHistoryUseCase = dependencies.observeConversionHistoryUseCase,
                    observeFavoriteConversionsUseCase = dependencies.observeFavoriteConversionsUseCase,
                    observeUserPreferencesUseCase = dependencies.observeUserPreferencesUseCase,
                    recordConversionUseCase = dependencies.recordConversionUseCase,
                    clearConversionHistoryUseCase = dependencies.clearConversionHistoryUseCase,
                    toggleFavoriteConversionUseCase = dependencies.toggleFavoriteConversionUseCase,
                    removeFavoriteConversionUseCase = dependencies.removeFavoriteConversionUseCase,
                    updateLastSelectedCategoryUseCase = dependencies.updateLastSelectedCategoryUseCase,
                    updateThemeModeUseCase = dependencies.updateThemeModeUseCase,
                    savedStateHandle = createSavedStateHandle(),
                )
            }
        },
    )

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    UnitConverterTheme(themeMode = uiState.value.themeMode) {
        ConverterRoute(
            uiState = uiState.value,
            snackbarMessages = viewModel.snackbarMessages,
            onAction = viewModel::onAction,
        )
    }
}
