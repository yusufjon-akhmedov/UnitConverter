package com.yusufjon.unitconverter.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.domain.model.UnitDefinition
import com.yusufjon.unitconverter.presentation.state.ConverterResultUiState
import com.yusufjon.unitconverter.presentation.state.ConverterTestTags
import com.yusufjon.unitconverter.presentation.state.UnitPickerTarget

@Composable
fun ConversionWorkspaceCard(
    selectedCategory: UnitCategory,
    inputValue: String,
    fromUnit: UnitDefinition,
    toUnit: UnitDefinition,
    isCurrentFavorite: Boolean,
    resultState: ConverterResultUiState,
    onInputChanged: (String) -> Unit,
    onOpenUnitPicker: (UnitPickerTarget) -> Unit,
    onSwapUnits: () -> Unit,
    onClearInput: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(),
        shape = RoundedCornerShape(28.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = selectedCategory.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = selectedCategory.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                FilledTonalIconButton(
                    modifier = Modifier.testTag(ConverterTestTags.FAVORITE_BUTTON),
                    onClick = onToggleFavorite,
                ) {
                    Icon(
                        imageVector = if (isCurrentFavorite) {
                            Icons.Outlined.Star
                        } else {
                            Icons.Outlined.StarOutline
                        },
                        contentDescription = if (isCurrentFavorite) {
                            "Remove current conversion from favorites"
                        } else {
                            "Save current conversion to favorites"
                        },
                    )
                }
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ConverterTestTags.INPUT_FIELD),
                value = inputValue,
                onValueChange = onInputChanged,
                singleLine = true,
                label = { Text("Value to convert") },
                placeholder = { Text("Example: 42.5") },
                isError = resultState is ConverterResultUiState.Invalid,
                supportingText = {
                    val supportingText = when (resultState) {
                        is ConverterResultUiState.Invalid -> resultState.message
                        else -> "Supports decimals and negative values where they make sense."
                    }
                    Text(supportingText)
                },
                trailingIcon = {
                    IconButton(
                        modifier = Modifier.testTag(ConverterTestTags.CLEAR_BUTTON),
                        onClick = onClearInput,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "Clear input",
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                UnitSelector(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(ConverterTestTags.FROM_UNIT_BUTTON),
                    label = "From",
                    unit = fromUnit,
                    onClick = { onOpenUnitPicker(UnitPickerTarget.INPUT) },
                )

                FilledTonalIconButton(
                    modifier = Modifier.testTag(ConverterTestTags.SWAP_BUTTON),
                    onClick = onSwapUnits,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.CompareArrows,
                        contentDescription = "Swap units",
                    )
                }

                UnitSelector(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(ConverterTestTags.TO_UNIT_BUTTON),
                    label = "To",
                    unit = toUnit,
                    onClick = { onOpenUnitPicker(UnitPickerTarget.OUTPUT) },
                )
            }

            AnimatedContent(
                targetState = resultState,
                label = "result_state_animation",
            ) { state ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    tonalElevation = 2.dp,
                ) {
                    when (state) {
                        ConverterResultUiState.Empty -> ResultPlaceholder()
                        is ConverterResultUiState.Invalid -> ResultError(message = state.message)
                        is ConverterResultUiState.Success -> ResultSuccess(state = state)
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(
                    onClick = onToggleFavorite,
                    colors = ButtonDefaults.filledTonalButtonColors(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PushPin,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isCurrentFavorite) "Pinned" else "Pin pair")
                }

                OutlinedButton(onClick = onClearInput) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
private fun UnitSelector(
    label: String,
    unit: UnitDefinition,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = unit.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = unit.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ResultPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Result",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Start typing to see a formatted conversion.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ResultError(
    message: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "Unable to convert",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun ResultSuccess(
    state: ConverterResultUiState.Success,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Result",
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                modifier = Modifier.testTag(ConverterTestTags.RESULT_VALUE),
                text = state.value,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = state.unitSymbol,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Text(
            text = state.summary,
            style = MaterialTheme.typography.bodyLarge,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = CircleShape,
                    ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = state.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
