package com.mtprorun.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mtprorun.domain.model.FilterState
import com.mtprorun.domain.model.SortOption
import com.mtprorun.presentation.theme.ChipBackground
import com.mtprorun.presentation.theme.ChipBorder
import com.mtprorun.presentation.theme.PrimaryColor
import com.mtprorun.presentation.theme.TextPrimary
import com.mtprorun.presentation.theme.TextSecondary
import com.mtprorun.ui.utils.toFlagEmoji

@Composable
fun FilterBottomSheet(
    countries: List<String>,
    currentFilters: FilterState,
    onDismiss: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    var selectedCountries by remember { mutableStateOf(currentFilters.selectedCountries) }
    var maxPing by remember { mutableIntStateOf(currentFilters.maxPingMs ?: 1000) }
    var onlyOnline by remember { mutableStateOf(currentFilters.onlyOnline) }
    var sortBy by remember { mutableStateOf(currentFilters.sortBy) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Filters", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Countries", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(countries) { country ->
                val isSelected = selectedCountries.contains(country)
                val flag = country.toFlagEmoji()
                SuggestionChip(
                    onClick = {
                        selectedCountries = if (isSelected) {
                            selectedCountries - country
                        } else {
                            selectedCountries + country
                        }
                    },
                    label = { Text("$flag $country") },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) PrimaryColor else ChipBackground,
                        labelColor = if (isSelected) TextPrimary else TextSecondary
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) PrimaryColor else ChipBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Max Ping: ${maxPing}ms", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Slider(
            value = maxPing.toFloat(),
            onValueChange = { maxPing = it.toInt() },
            valueRange = 0f..1000f,
            steps = 19
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Only Online", color = TextPrimary)
            Switch(
                checked = onlyOnline,
                onCheckedChange = { onlyOnline = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Sort By", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        SortOption.entries.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { sortBy = option }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(option.displayName, color = TextPrimary)
                RadioButton(
                    selected = sortBy == option,
                    onClick = { sortBy = option }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    selectedCountries = emptySet()
                    maxPing = 1000
                    onlyOnline = false
                    sortBy = SortOption.PING_ASC
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }

            Button(
                onClick = {
                    onApply(
                        FilterState(
                            selectedCountries = selectedCountries,
                            maxPingMs = if (maxPing < 1000) maxPing else null,
                            onlyOnline = onlyOnline,
                            sortBy = sortBy
                        )
                    )
                    onDismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Apply")
            }
        }
    }
}

val SortOption.displayName: String
    get() = when (this) {
        SortOption.PING_ASC -> "Ping: Low to High"
        SortOption.PING_DESC -> "Ping: High to Low"
        SortOption.COUNTRY -> "Country"
        SortOption.ADDED -> "Recently Added"
    }
