package com.yusufjon.unitconverter.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.yusufjon.unitconverter.domain.model.UnitDefinition
import com.yusufjon.unitconverter.presentation.state.ConverterTestTags
import com.yusufjon.unitconverter.presentation.state.UnitPickerUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitPickerBottomSheet(
    state: UnitPickerUiState,
    onDismiss: () -> Unit,
    onUnitSelected: (String) -> Unit,
) {
    var searchQuery by rememberSaveable(state.target, state.selectedUnitId) { mutableStateOf("") }
    val filteredUnits = state.units.filter { unit ->
        unit.matches(searchQuery)
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleLarge,
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ConverterTestTags.UNIT_PICKER_SEARCH),
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                label = { Text("Search units") },
                placeholder = { Text("Type a name or symbol") },
            )

            if (filteredUnits.isEmpty()) {
                Text(
                    text = "No units match that search.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn {
                    items(filteredUnits, key = UnitDefinition::id) { unit ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onUnitSelected(unit.id) }
                                .testTag(ConverterTestTags.unitOption(unit.id)),
                            headlineContent = { Text(unit.displayName) },
                            supportingContent = { Text(unit.symbol) },
                            trailingContent = {
                                if (state.selectedUnitId == unit.id) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                    )
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}
