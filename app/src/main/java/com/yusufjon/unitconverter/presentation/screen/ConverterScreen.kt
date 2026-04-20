package com.yusufjon.unitconverter.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.BrightnessHigh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yusufjon.unitconverter.domain.model.ThemeMode
import com.yusufjon.unitconverter.presentation.components.CategorySelector
import com.yusufjon.unitconverter.presentation.components.ConversionWorkspaceCard
import com.yusufjon.unitconverter.presentation.components.FavoritesSection
import com.yusufjon.unitconverter.presentation.components.HistorySection
import com.yusufjon.unitconverter.presentation.components.UnitPickerBottomSheet
import com.yusufjon.unitconverter.presentation.state.ConverterAction
import com.yusufjon.unitconverter.presentation.state.ConverterUiState
import kotlinx.coroutines.flow.Flow

@Composable
fun ConverterRoute(
    uiState: ConverterUiState,
    snackbarMessages: Flow<String>,
    onAction: (ConverterAction) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessages) {
        snackbarMessages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    ConverterScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onAction = onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConverterScreen(
    uiState: ConverterUiState,
    snackbarHostState: SnackbarHostState,
    onAction: (ConverterAction) -> Unit,
) {
    var themeMenuExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.20f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            ),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopAppBar(
                    title = { Text("Unit Converter") },
                    actions = {
                        IconButton(onClick = { themeMenuExpanded = true }) {
                            Icon(
                                imageVector = themeModeIcon(uiState.themeMode),
                                contentDescription = "Change theme",
                            )
                        }
                        DropdownMenu(
                            expanded = themeMenuExpanded,
                            onDismissRequest = { themeMenuExpanded = false },
                        ) {
                            ThemeMode.entries.forEach { themeMode ->
                                DropdownMenuItem(
                                    text = { Text(themeMode.displayName()) },
                                    onClick = {
                                        themeMenuExpanded = false
                                        onAction(ConverterAction.ThemeModeSelected(themeMode))
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = themeModeIcon(themeMode),
                                            contentDescription = null,
                                        )
                                    },
                                )
                            }
                        }
                    },
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                state = listState,
            ) {
                item {
                    HeroSection(
                        selectedCategory = uiState.selectedCategory,
                    )
                }

                item {
                    CategorySelector(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { category ->
                            onAction(ConverterAction.CategorySelected(category))
                        },
                    )
                }

                item {
                    BoxWithConstraints {
                        val isWideLayout = maxWidth >= 900.dp
                        if (isWideLayout) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                ConversionWorkspaceCard(
                                    modifier = Modifier.weight(1.15f),
                                    selectedCategory = uiState.selectedCategory,
                                    inputValue = uiState.inputValue,
                                    fromUnit = uiState.fromUnit,
                                    toUnit = uiState.toUnit,
                                    isCurrentFavorite = uiState.isCurrentFavorite,
                                    resultState = uiState.resultState,
                                    onInputChanged = { onAction(ConverterAction.InputChanged(it)) },
                                    onOpenUnitPicker = { target ->
                                        onAction(ConverterAction.OpenUnitPicker(target))
                                    },
                                    onSwapUnits = { onAction(ConverterAction.SwapUnits) },
                                    onClearInput = { onAction(ConverterAction.ClearInput) },
                                    onToggleFavorite = { onAction(ConverterAction.ToggleFavorite) },
                                )

                                Column(
                                    modifier = Modifier.weight(0.85f),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                ) {
                                    FavoritesSection(
                                        favorites = uiState.favorites,
                                        onApplyFavorite = { id ->
                                            onAction(ConverterAction.ApplyFavorite(id))
                                        },
                                        onRemoveFavorite = { id ->
                                            onAction(ConverterAction.RemoveFavorite(id))
                                        },
                                    )
                                    HistorySection(
                                        history = uiState.history,
                                        onApplyHistoryItem = { id ->
                                            onAction(ConverterAction.ApplyHistoryItem(id))
                                        },
                                        onClearHistory = {
                                            onAction(ConverterAction.ClearHistory)
                                        },
                                    )
                                }
                            }
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                            ) {
                                ConversionWorkspaceCard(
                                    selectedCategory = uiState.selectedCategory,
                                    inputValue = uiState.inputValue,
                                    fromUnit = uiState.fromUnit,
                                    toUnit = uiState.toUnit,
                                    isCurrentFavorite = uiState.isCurrentFavorite,
                                    resultState = uiState.resultState,
                                    onInputChanged = { onAction(ConverterAction.InputChanged(it)) },
                                    onOpenUnitPicker = { target ->
                                        onAction(ConverterAction.OpenUnitPicker(target))
                                    },
                                    onSwapUnits = { onAction(ConverterAction.SwapUnits) },
                                    onClearInput = { onAction(ConverterAction.ClearInput) },
                                    onToggleFavorite = { onAction(ConverterAction.ToggleFavorite) },
                                )
                                FavoritesSection(
                                    favorites = uiState.favorites,
                                    onApplyFavorite = { id ->
                                        onAction(ConverterAction.ApplyFavorite(id))
                                    },
                                    onRemoveFavorite = { id ->
                                        onAction(ConverterAction.RemoveFavorite(id))
                                    },
                                )
                                HistorySection(
                                    history = uiState.history,
                                    onApplyHistoryItem = { id ->
                                        onAction(ConverterAction.ApplyHistoryItem(id))
                                    },
                                    onClearHistory = {
                                        onAction(ConverterAction.ClearHistory)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        uiState.activeUnitPicker?.let { pickerState ->
            UnitPickerBottomSheet(
                state = pickerState,
                onDismiss = { onAction(ConverterAction.DismissUnitPicker) },
                onUnitSelected = { unitId ->
                    onAction(
                        ConverterAction.UnitSelected(
                            target = pickerState.target,
                            unitId = unitId,
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun HeroSection(
    selectedCategory: com.yusufjon.unitconverter.domain.model.UnitCategory,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        tonalElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.90f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        ),
                    ),
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "A cleaner way to convert everyday values",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Explore 7 categories, pin repeat conversions, and revisit recent work without leaving the main screen.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HeroMetric(
                    label = "Categories",
                    value = "7",
                )
                HeroMetric(
                    label = "Current focus",
                    value = selectedCategory.displayName,
                )
            }
        }
    }
}

@Composable
private fun HeroMetric(
    label: String,
    value: String,
) {
    Column(
        modifier = Modifier.width(132.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun themeModeIcon(themeMode: ThemeMode) = when (themeMode) {
    ThemeMode.SYSTEM -> Icons.Outlined.BrightnessAuto
    ThemeMode.LIGHT -> Icons.Outlined.BrightnessHigh
    ThemeMode.DARK -> Icons.Outlined.Brightness4
}

private fun ThemeMode.displayName(): String = when (this) {
    ThemeMode.SYSTEM -> "System"
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
}
