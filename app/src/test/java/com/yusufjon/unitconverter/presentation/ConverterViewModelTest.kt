package com.yusufjon.unitconverter.presentation

import androidx.lifecycle.SavedStateHandle
import com.yusufjon.unitconverter.domain.converter.NumericInputParser
import com.yusufjon.unitconverter.domain.converter.UnitCatalog
import com.yusufjon.unitconverter.domain.converter.ValueFormatter
import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.usecase.ClearConversionHistoryUseCase
import com.yusufjon.unitconverter.domain.usecase.ConvertValueUseCase
import com.yusufjon.unitconverter.domain.usecase.ObserveConversionHistoryUseCase
import com.yusufjon.unitconverter.domain.usecase.ObserveFavoriteConversionsUseCase
import com.yusufjon.unitconverter.domain.usecase.ObserveUserPreferencesUseCase
import com.yusufjon.unitconverter.domain.usecase.RecordConversionUseCase
import com.yusufjon.unitconverter.domain.usecase.RemoveFavoriteConversionUseCase
import com.yusufjon.unitconverter.domain.usecase.ToggleFavoriteConversionUseCase
import com.yusufjon.unitconverter.domain.usecase.UpdateLastSelectedCategoryUseCase
import com.yusufjon.unitconverter.domain.usecase.UpdateThemeModeUseCase
import com.yusufjon.unitconverter.presentation.state.ConverterAction
import com.yusufjon.unitconverter.presentation.state.ConverterResultUiState
import com.yusufjon.unitconverter.presentation.viewmodel.ConverterViewModel
import com.yusufjon.unitconverter.util.FakeConversionHistoryRepository
import com.yusufjon.unitconverter.util.FakeFavoriteConversionRepository
import com.yusufjon.unitconverter.util.FakeUserPreferencesRepository
import com.yusufjon.unitconverter.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var historyRepository: FakeConversionHistoryRepository
    private lateinit var favoriteRepository: FakeFavoriteConversionRepository
    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: ConverterViewModel

    @Before
    fun setUp() {
        historyRepository = FakeConversionHistoryRepository()
        favoriteRepository = FakeFavoriteConversionRepository()
        userPreferencesRepository = FakeUserPreferencesRepository()

        val unitCatalog = UnitCatalog()
        val valueFormatter = ValueFormatter()
        viewModel = ConverterViewModel(
            unitCatalog = unitCatalog,
            valueFormatter = valueFormatter,
            convertValueUseCase = ConvertValueUseCase(
                unitCatalog = unitCatalog,
                inputParser = NumericInputParser(),
                valueFormatter = valueFormatter,
            ),
            observeConversionHistoryUseCase = ObserveConversionHistoryUseCase(historyRepository),
            observeFavoriteConversionsUseCase = ObserveFavoriteConversionsUseCase(favoriteRepository),
            observeUserPreferencesUseCase = ObserveUserPreferencesUseCase(userPreferencesRepository),
            recordConversionUseCase = RecordConversionUseCase(historyRepository),
            clearConversionHistoryUseCase = ClearConversionHistoryUseCase(historyRepository),
            toggleFavoriteConversionUseCase = ToggleFavoriteConversionUseCase(favoriteRepository),
            removeFavoriteConversionUseCase = RemoveFavoriteConversionUseCase(favoriteRepository),
            updateLastSelectedCategoryUseCase = UpdateLastSelectedCategoryUseCase(userPreferencesRepository),
            updateThemeModeUseCase = UpdateThemeModeUseCase(userPreferencesRepository),
            savedStateHandle = SavedStateHandle(),
        )
    }

    @Test
    fun `temperature input updates result and records history after debounce`() = runTest(mainDispatcherRule.testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.onAction(ConverterAction.CategorySelected(UnitCategory.TEMPERATURE))
        viewModel.onAction(ConverterAction.InputChanged("100"))
        runCurrent()

        val resultState = viewModel.uiState.value.resultState as ConverterResultUiState.Success
        assertEquals("212", resultState.value)
        assertTrue(historyRepository.items().isEmpty())

        advanceTimeBy(700)
        advanceUntilIdle()

        assertEquals(1, historyRepository.items().size)
        assertEquals(UnitCategory.TEMPERATURE, userPreferencesRepository.currentPreferences().lastSelectedCategory)
        collectJob.cancel()
    }

    @Test
    fun `swap units recomputes the result`() = runTest(mainDispatcherRule.testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.onAction(ConverterAction.CategorySelected(UnitCategory.TEMPERATURE))
        viewModel.onAction(ConverterAction.InputChanged("32"))
        viewModel.onAction(ConverterAction.SwapUnits)
        advanceUntilIdle()

        val resultState = viewModel.uiState.value.resultState as ConverterResultUiState.Success
        assertEquals("0", resultState.value)
        collectJob.cancel()
    }

    @Test
    fun `toggling favorites adds and removes current conversion pair`() = runTest(mainDispatcherRule.testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect { } }
        assertFalse(viewModel.uiState.value.isCurrentFavorite)

        viewModel.onAction(ConverterAction.ToggleFavorite)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isCurrentFavorite)
        assertEquals(1, favoriteRepository.items().size)

        viewModel.onAction(ConverterAction.ToggleFavorite)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isCurrentFavorite)
        assertTrue(favoriteRepository.items().isEmpty())
        collectJob.cancel()
    }

    @Test
    fun `theme selection persists through preferences`() = runTest(mainDispatcherRule.testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect { } }
        viewModel.onAction(ConverterAction.ThemeModeSelected(ThemeMode.DARK))
        advanceUntilIdle()

        assertEquals(ThemeMode.DARK, userPreferencesRepository.currentPreferences().themeMode)
        collectJob.cancel()
    }
}
