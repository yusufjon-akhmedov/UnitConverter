package com.yusufjon.unitconverter.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yusufjon.unitconverter.domain.converter.UnitCatalog
import com.yusufjon.unitconverter.domain.converter.ValueFormatter
import com.yusufjon.unitconverter.domain.model.ConversionOutcome
import com.yusufjon.unitconverter.domain.model.ConversionRequest
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
import com.yusufjon.unitconverter.presentation.state.ConversionHistoryUiModel
import com.yusufjon.unitconverter.presentation.state.ConverterAction
import com.yusufjon.unitconverter.presentation.state.ConverterResultUiState
import com.yusufjon.unitconverter.presentation.state.ConverterUiState
import com.yusufjon.unitconverter.presentation.state.FavoriteConversionUiModel
import com.yusufjon.unitconverter.presentation.state.UnitPickerTarget
import com.yusufjon.unitconverter.presentation.state.UnitPickerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ConverterViewModel(
    private val unitCatalog: UnitCatalog,
    private val valueFormatter: ValueFormatter,
    private val convertValueUseCase: ConvertValueUseCase,
    observeConversionHistoryUseCase: ObserveConversionHistoryUseCase,
    observeFavoriteConversionsUseCase: ObserveFavoriteConversionsUseCase,
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val recordConversionUseCase: RecordConversionUseCase,
    private val clearConversionHistoryUseCase: ClearConversionHistoryUseCase,
    private val toggleFavoriteConversionUseCase: ToggleFavoriteConversionUseCase,
    private val removeFavoriteConversionUseCase: RemoveFavoriteConversionUseCase,
    private val updateLastSelectedCategoryUseCase: UpdateLastSelectedCategoryUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val hasRestoredEditorState = EDITOR_STATE_KEYS.any(savedStateHandle::contains)
    private val defaultCategory = UnitCategory.fromName(
        savedStateHandle[STATE_SELECTED_CATEGORY],
    )

    private val editorState = MutableStateFlow(
        loadInitialEditorState(defaultCategory),
    )

    private val snackbarMessagesMutable = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val snackbarMessages = snackbarMessagesMutable.asSharedFlow()

    private val historyFlow = observeConversionHistoryUseCase()
    private val favoritesFlow = observeFavoriteConversionsUseCase()
    private val preferencesFlow = observeUserPreferencesUseCase()

    private var pendingHistoryRecordJob: Job? = null
    private var lastRecordedSignature: String? = null
    private var initialPreferencesApplied = false
    private var userHasInteracted = false

    val uiState: StateFlow<ConverterUiState> = combine(
        editorState,
        favoritesFlow,
        historyFlow,
        preferencesFlow,
    ) { editor, favorites, history, preferences ->
        val definition = unitCatalog.definitionFor(editor.selectedCategory)
        val fromUnit = definition.inputUnitOrDefault(editor.fromUnitId)
        val toUnit = definition.outputUnitOrDefault(editor.toUnitId)
        val outcome = convertValueUseCase(
            ConversionRequest(
                rawInput = editor.inputValue,
                category = editor.selectedCategory,
                fromUnitId = fromUnit.id,
                toUnitId = toUnit.id,
            ),
        )

        val favoritesUiModels = favorites.map { favorite ->
            val favoriteDefinition = unitCatalog.definitionFor(favorite.category)
            FavoriteConversionUiModel(
                id = favorite.id,
                category = favorite.category,
                fromUnit = favoriteDefinition.inputUnitOrDefault(favorite.fromUnitId),
                toUnit = favoriteDefinition.outputUnitOrDefault(favorite.toUnitId),
                createdAtMillis = favorite.createdAtMillis,
            )
        }

        val historyUiModels = history.map { item ->
            val historyDefinition = unitCatalog.definitionFor(item.category)
            ConversionHistoryUiModel(
                id = item.id,
                category = item.category,
                fromUnit = historyDefinition.inputUnitOrDefault(item.fromUnitId),
                toUnit = historyDefinition.outputUnitOrDefault(item.toUnitId),
                inputDisplayValue = valueFormatter.format(item.inputValue),
                outputDisplayValue = valueFormatter.format(item.outputValue),
                createdAtMillis = item.createdAtMillis,
            )
        }

        ConverterUiState(
            isLoading = false,
            categories = unitCatalog.categories(),
            selectedCategory = editor.selectedCategory,
            inputValue = editor.inputValue,
            fromUnit = fromUnit,
            toUnit = toUnit,
            resultState = outcome.toUiState(),
            favorites = favoritesUiModels,
            history = historyUiModels,
            isCurrentFavorite = favorites.any { favorite ->
                favorite.category == editor.selectedCategory &&
                    favorite.fromUnitId == fromUnit.id &&
                    favorite.toUnitId == toUnit.id
            },
            activeUnitPicker = editor.activeUnitPickerTarget?.let { target ->
                UnitPickerUiState(
                    target = target,
                    title = if (target == UnitPickerTarget.INPUT) {
                        "Select input unit"
                    } else {
                        "Select output unit"
                    },
                    units = definition.units,
                    selectedUnitId = if (target == UnitPickerTarget.INPUT) {
                        fromUnit.id
                    } else {
                        toUnit.id
                    },
                )
            },
            themeMode = preferences.themeMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = createInitialUiState(),
    )

    init {
        observeInitialPreferences()
    }

    fun onAction(action: ConverterAction) {
        if (action !is ConverterAction.ThemeModeSelected) {
            userHasInteracted = true
        }

        when (action) {
            is ConverterAction.InputChanged -> {
                updateEditorState { copy(inputValue = action.value) }
                scheduleHistoryRecording()
            }

            is ConverterAction.CategorySelected -> {
                applyCategorySelection(
                    category = action.category,
                    persistPreference = true,
                    scheduleHistory = true,
                )
            }

            is ConverterAction.OpenUnitPicker -> {
                updateEditorState { copy(activeUnitPickerTarget = action.target) }
            }

            ConverterAction.DismissUnitPicker -> {
                updateEditorState { copy(activeUnitPickerTarget = null) }
            }

            is ConverterAction.UnitSelected -> {
                updateEditorState {
                    when (action.target) {
                        UnitPickerTarget.INPUT -> copy(
                            fromUnitId = action.unitId,
                            activeUnitPickerTarget = null,
                        )
                        UnitPickerTarget.OUTPUT -> copy(
                            toUnitId = action.unitId,
                            activeUnitPickerTarget = null,
                        )
                    }
                }
                scheduleHistoryRecording()
            }

            ConverterAction.SwapUnits -> {
                updateEditorState {
                    copy(
                        fromUnitId = toUnitId,
                        toUnitId = fromUnitId,
                    )
                }
                scheduleHistoryRecording()
            }

            ConverterAction.ClearInput -> {
                pendingHistoryRecordJob?.cancel()
                updateEditorState { copy(inputValue = "") }
            }

            ConverterAction.ToggleFavorite -> toggleFavorite()

            is ConverterAction.RemoveFavorite -> {
                viewModelScope.launch {
                    removeFavoriteConversionUseCase(action.favoriteId)
                    snackbarMessagesMutable.emit("Favorite removed.")
                }
            }

            is ConverterAction.ApplyFavorite -> {
                val favorite = uiState.value.favorites.firstOrNull { it.id == action.favoriteId } ?: return
                applyCategorySelection(
                    category = favorite.category,
                    persistPreference = true,
                    scheduleHistory = false,
                )
                updateEditorState {
                    copy(
                        selectedCategory = favorite.category,
                        fromUnitId = favorite.fromUnit.id,
                        toUnitId = favorite.toUnit.id,
                    )
                }
            }

            is ConverterAction.ApplyHistoryItem -> {
                val historyItem = uiState.value.history.firstOrNull { it.id == action.historyId } ?: return
                applyCategorySelection(
                    category = historyItem.category,
                    persistPreference = true,
                    scheduleHistory = false,
                )
                updateEditorState {
                    copy(
                        selectedCategory = historyItem.category,
                        inputValue = historyItem.inputDisplayValue,
                        fromUnitId = historyItem.fromUnit.id,
                        toUnitId = historyItem.toUnit.id,
                    )
                }
            }

            ConverterAction.ClearHistory -> {
                viewModelScope.launch {
                    clearConversionHistoryUseCase()
                    snackbarMessagesMutable.emit("History cleared.")
                }
            }

            is ConverterAction.ThemeModeSelected -> {
                viewModelScope.launch {
                    updateThemeModeUseCase(action.themeMode)
                }
            }
        }
    }

    private fun observeInitialPreferences() {
        viewModelScope.launch {
            preferencesFlow.collect { preferences ->
                if (!initialPreferencesApplied && !hasRestoredEditorState && !userHasInteracted) {
                    initialPreferencesApplied = true
                    applyCategorySelection(
                        category = preferences.lastSelectedCategory,
                        persistPreference = false,
                        scheduleHistory = false,
                    )
                } else {
                    initialPreferencesApplied = true
                }
            }
        }
    }

    private fun toggleFavorite() {
        val currentState = uiState.value
        viewModelScope.launch {
            toggleFavoriteConversionUseCase(
                isFavorite = currentState.isCurrentFavorite,
                category = currentState.selectedCategory,
                fromUnitId = currentState.fromUnit.id,
                toUnitId = currentState.toUnit.id,
            )
            snackbarMessagesMutable.emit(
                if (currentState.isCurrentFavorite) {
                    "Removed from favorites."
                } else {
                    "Saved to favorites."
                },
            )
        }
    }

    private fun applyCategorySelection(
        category: UnitCategory,
        persistPreference: Boolean,
        scheduleHistory: Boolean,
    ) {
        val definition = unitCatalog.definitionFor(category)
        updateEditorState {
            copy(
                selectedCategory = category,
                fromUnitId = definition.defaultFromUnitId,
                toUnitId = definition.defaultToUnitId,
                activeUnitPickerTarget = null,
            )
        }

        if (persistPreference) {
            viewModelScope.launch {
                updateLastSelectedCategoryUseCase(category)
            }
        }

        if (scheduleHistory) {
            scheduleHistoryRecording()
        }
    }

    private fun scheduleHistoryRecording() {
        val outcome = buildCurrentOutcome()
        val snapshot = (outcome as? ConversionOutcome.Success)?.snapshot ?: run {
            pendingHistoryRecordJob?.cancel()
            return
        }

        if (snapshot.signature == lastRecordedSignature) {
            return
        }

        pendingHistoryRecordJob?.cancel()
        pendingHistoryRecordJob = viewModelScope.launch {
            delay(AUTO_RECORD_DELAY_MILLIS)
            recordConversionUseCase(snapshot)
            lastRecordedSignature = snapshot.signature
        }
    }

    private fun buildCurrentOutcome(): ConversionOutcome {
        val currentEditorState = editorState.value
        return convertValueUseCase(
            ConversionRequest(
                rawInput = currentEditorState.inputValue,
                category = currentEditorState.selectedCategory,
                fromUnitId = currentEditorState.fromUnitId,
                toUnitId = currentEditorState.toUnitId,
            ),
        )
    }

    private fun updateEditorState(transform: EditorState.() -> EditorState) {
        val updatedState = editorState.value.transform()
        editorState.value = updatedState
        savedStateHandle[STATE_SELECTED_CATEGORY] = updatedState.selectedCategory.name
        savedStateHandle[STATE_INPUT_VALUE] = updatedState.inputValue
        savedStateHandle[STATE_FROM_UNIT_ID] = updatedState.fromUnitId
        savedStateHandle[STATE_TO_UNIT_ID] = updatedState.toUnitId
    }

    private fun loadInitialEditorState(category: UnitCategory): EditorState {
        val definition = unitCatalog.definitionFor(category)
        return EditorState(
            selectedCategory = category,
            inputValue = savedStateHandle[STATE_INPUT_VALUE] ?: "",
            fromUnitId = savedStateHandle[STATE_FROM_UNIT_ID] ?: definition.defaultFromUnitId,
            toUnitId = savedStateHandle[STATE_TO_UNIT_ID] ?: definition.defaultToUnitId,
            activeUnitPickerTarget = null,
        )
    }

    private fun createInitialUiState(): ConverterUiState {
        val editor = editorState.value
        val definition = unitCatalog.definitionFor(editor.selectedCategory)
        val fromUnit = definition.inputUnitOrDefault(editor.fromUnitId)
        val toUnit = definition.outputUnitOrDefault(editor.toUnitId)

        return ConverterUiState(
            isLoading = false,
            categories = unitCatalog.categories(),
            selectedCategory = editor.selectedCategory,
            inputValue = editor.inputValue,
            fromUnit = fromUnit,
            toUnit = toUnit,
            resultState = buildCurrentOutcome().toUiState(),
            themeMode = ThemeMode.SYSTEM,
        )
    }

    private fun ConversionOutcome.toUiState(): ConverterResultUiState {
        return when (this) {
            ConversionOutcome.Empty -> ConverterResultUiState.Empty
            is ConversionOutcome.Invalid -> ConverterResultUiState.Invalid(message)
            is ConversionOutcome.Success -> ConverterResultUiState.Success(
                value = snapshot.formattedOutput,
                unitSymbol = snapshot.toUnit.symbol,
                summary = "${snapshot.formattedInput} ${snapshot.fromUnit.symbol} = ${snapshot.formattedOutput} ${snapshot.toUnit.symbol}",
                detail = "${snapshot.fromUnit.displayName} to ${snapshot.toUnit.displayName}",
            )
        }
    }

    data class Dependencies(
        val unitCatalog: UnitCatalog,
        val valueFormatter: ValueFormatter,
        val convertValueUseCase: ConvertValueUseCase,
        val observeConversionHistoryUseCase: ObserveConversionHistoryUseCase,
        val observeFavoriteConversionsUseCase: ObserveFavoriteConversionsUseCase,
        val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
        val recordConversionUseCase: RecordConversionUseCase,
        val clearConversionHistoryUseCase: ClearConversionHistoryUseCase,
        val toggleFavoriteConversionUseCase: ToggleFavoriteConversionUseCase,
        val removeFavoriteConversionUseCase: RemoveFavoriteConversionUseCase,
        val updateLastSelectedCategoryUseCase: UpdateLastSelectedCategoryUseCase,
        val updateThemeModeUseCase: UpdateThemeModeUseCase,
    )

    private data class EditorState(
        val selectedCategory: UnitCategory,
        val inputValue: String,
        val fromUnitId: String,
        val toUnitId: String,
        val activeUnitPickerTarget: UnitPickerTarget?,
    )

    private companion object {
        const val AUTO_RECORD_DELAY_MILLIS = 700L
        const val STATE_SELECTED_CATEGORY = "selected_category"
        const val STATE_INPUT_VALUE = "input_value"
        const val STATE_FROM_UNIT_ID = "from_unit_id"
        const val STATE_TO_UNIT_ID = "to_unit_id"

        val EDITOR_STATE_KEYS = listOf(
            STATE_SELECTED_CATEGORY,
            STATE_INPUT_VALUE,
            STATE_FROM_UNIT_ID,
            STATE_TO_UNIT_ID,
        )
    }
}
