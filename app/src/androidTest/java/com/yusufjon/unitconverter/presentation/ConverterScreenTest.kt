package com.yusufjon.unitconverter.presentation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yusufjon.unitconverter.domain.converter.NumericInputParser
import com.yusufjon.unitconverter.domain.converter.UnitCatalog
import com.yusufjon.unitconverter.domain.converter.ValueFormatter
import com.yusufjon.unitconverter.domain.model.ConversionHistoryItem
import com.yusufjon.unitconverter.domain.model.ConversionSnapshot
import com.yusufjon.unitconverter.domain.model.FavoriteConversion
import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UserPreferences
import com.yusufjon.unitconverter.domain.repository.ConversionHistoryRepository
import com.yusufjon.unitconverter.domain.repository.FavoriteConversionRepository
import com.yusufjon.unitconverter.domain.repository.UserPreferencesRepository
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
import com.yusufjon.unitconverter.presentation.screen.ConverterRoute
import com.yusufjon.unitconverter.presentation.state.ConverterTestTags
import com.yusufjon.unitconverter.presentation.theme.UnitConverterTheme
import com.yusufjon.unitconverter.presentation.viewmodel.ConverterViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConverterScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: ConverterViewModel

    @Before
    fun setUp() {
        val unitCatalog = UnitCatalog()
        val valueFormatter = ValueFormatter()
        val historyRepository = FakeConversionHistoryRepository()
        val favoriteRepository = FakeFavoriteConversionRepository()
        val userPreferencesRepository = FakeUserPreferencesRepository()

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

        composeRule.setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            UnitConverterTheme(themeMode = uiState.themeMode) {
                ConverterRoute(
                    uiState = uiState,
                    snackbarMessages = viewModel.snackbarMessages,
                    onAction = viewModel::onAction,
                )
            }
        }
    }

    @Test
    fun conversion_flow_updates_result_when_category_units_and_swap_change() {
        composeRule.onNodeWithTag(ConverterTestTags.categoryChip(UnitCategory.TEMPERATURE))
            .performClick()
        composeRule.onNodeWithTag(ConverterTestTags.INPUT_FIELD).performTextInput("100")
        composeRule.onNodeWithTag(ConverterTestTags.TO_UNIT_BUTTON).performClick()
        composeRule.onNodeWithTag(ConverterTestTags.UNIT_PICKER_SEARCH).performTextInput("kel")
        composeRule.onNodeWithTag(ConverterTestTags.unitOption("kelvin")).performClick()

        composeRule.onNodeWithTag(ConverterTestTags.RESULT_VALUE).assertTextContains("373.15")

        composeRule.onNodeWithTag(ConverterTestTags.SWAP_BUTTON).performClick()

        composeRule.onNodeWithTag(ConverterTestTags.RESULT_VALUE).assertTextContains("-173.15")
    }
}

private class FakeConversionHistoryRepository : ConversionHistoryRepository {
    private val historyItems = MutableStateFlow<List<ConversionHistoryItem>>(emptyList())
    private var nextId = 1L

    override fun observeRecent(limit: Int): Flow<List<ConversionHistoryItem>> = historyItems.asStateFlow()

    override suspend fun record(snapshot: ConversionSnapshot) {
        historyItems.value = listOf(
            ConversionHistoryItem(
                id = nextId++,
                category = snapshot.category,
                fromUnitId = snapshot.fromUnit.id,
                toUnitId = snapshot.toUnit.id,
                inputValue = snapshot.inputValue,
                outputValue = snapshot.outputValue,
                createdAtMillis = nextId,
            ),
        ) + historyItems.value
    }

    override suspend fun clear() {
        historyItems.value = emptyList()
    }
}

private class FakeFavoriteConversionRepository : FavoriteConversionRepository {
    private val favorites = MutableStateFlow<List<FavoriteConversion>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<FavoriteConversion>> = favorites.asStateFlow()

    override suspend fun add(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        favorites.value = listOf(
            FavoriteConversion(
                id = nextId++,
                category = category,
                fromUnitId = fromUnitId,
                toUnitId = toUnitId,
                createdAtMillis = nextId,
            ),
        ) + favorites.value
    }

    override suspend fun remove(id: Long) {
        favorites.value = favorites.value.filterNot { it.id == id }
    }

    override suspend fun remove(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
    ) {
        favorites.value = favorites.value.filterNot {
            it.category == category && it.fromUnitId == fromUnitId && it.toUnitId == toUnitId
        }
    }
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val preferences = MutableStateFlow(UserPreferences(themeMode = ThemeMode.LIGHT))

    override fun observe(): Flow<UserPreferences> = preferences.asStateFlow()

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        preferences.value = preferences.value.copy(themeMode = themeMode)
    }

    override suspend fun setLastSelectedCategory(category: UnitCategory) {
        preferences.value = preferences.value.copy(lastSelectedCategory = category)
    }
}
