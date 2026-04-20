package com.yusufjon.unitconverter.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.LocalDrink
import androidx.compose.material.icons.outlined.CropSquare
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.yusufjon.unitconverter.domain.model.UnitCategory
import com.yusufjon.unitconverter.presentation.state.ConverterTestTags

@Composable
fun CategorySelector(
    categories: List<UnitCategory>,
    selectedCategory: UnitCategory,
    onCategorySelected: (UnitCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(categories) { category ->
            FilterChip(
                modifier = Modifier.testTag(ConverterTestTags.categoryChip(category)),
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = category.icon(),
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

private fun UnitCategory.icon(): ImageVector {
    return when (this) {
        UnitCategory.LENGTH -> Icons.Outlined.Straighten
        UnitCategory.MASS -> Icons.Outlined.Scale
        UnitCategory.TEMPERATURE -> Icons.Outlined.DeviceThermostat
        UnitCategory.VOLUME -> Icons.Outlined.LocalDrink
        UnitCategory.AREA -> Icons.Outlined.CropSquare
        UnitCategory.SPEED -> Icons.Outlined.Speed
        UnitCategory.TIME -> Icons.Outlined.AccessTime
    }
}
