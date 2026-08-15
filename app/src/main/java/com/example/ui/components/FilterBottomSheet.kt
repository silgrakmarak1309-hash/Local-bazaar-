package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ItemCondition
import com.example.model.ListingFilter
import com.example.model.LocalBazaarCategories
import com.example.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    currentFilter: ListingFilter,
    onDismiss: () -> Unit,
    onApplyFilter: (ListingFilter) -> Unit,
    onReset: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(currentFilter.category) }
    var selectedCondition by remember { mutableStateOf(currentFilter.condition) }
    var verifiedOnly by remember { mutableStateOf(currentFilter.verifiedOnly) }
    var featuredOnly by remember { mutableStateOf(currentFilter.featuredOnly) }
    var selectedSort by remember { mutableStateOf(currentFilter.sortOption) }
    var maxPriceSlider by remember { mutableStateOf(currentFilter.maxPrice?.toFloat() ?: 150000f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Sort Listings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort By
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            SortOption.values().forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedSort == opt,
                        onClick = { selectedSort = opt },
                        modifier = Modifier.testTag("sort_opt_${opt.name}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = opt.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedSort == opt) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Max Price Range Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Budget / Max Price",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (maxPriceSlider >= 150000f) "Any Price" else "Up to ₹%,d".format(maxPriceSlider.toLong()),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = maxPriceSlider,
                onValueChange = { maxPriceSlider = it },
                valueRange = 500f..150000f,
                steps = 29,
                modifier = Modifier.testTag("price_slider")
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Verified Sellers Only Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Verified Sellers Only",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Show only phone/business verified community members",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = verifiedOnly,
                    onCheckedChange = { verifiedOnly = it },
                    modifier = Modifier.testTag("verified_only_switch")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Featured & Boosted Only Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Featured Listings",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Top-ranked & promoted deals",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = featuredOnly,
                    onCheckedChange = { featuredOnly = it },
                    modifier = Modifier.testTag("featured_only_switch")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Condition
            Text(
                text = "Item Condition",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    null to "All",
                    ItemCondition.BRAND_NEW to "Brand New",
                    ItemCondition.LIKE_NEW to "Like New",
                    ItemCondition.GOOD to "Good"
                ).forEach { (cond, label) ->
                    val isSelected = selectedCondition == cond
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCondition = cond },
                        label = { Text(label) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("filter_cond_${label.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedCategory = null
                        selectedCondition = null
                        verifiedOnly = false
                        featuredOnly = false
                        selectedSort = SortOption.NEWEST
                        maxPriceSlider = 150000f
                        onReset()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("reset_filters_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset All")
                }

                Button(
                    onClick = {
                        onApplyFilter(
                            currentFilter.copy(
                                category = selectedCategory,
                                condition = selectedCondition,
                                verifiedOnly = verifiedOnly,
                                featuredOnly = featuredOnly,
                                sortOption = selectedSort,
                                maxPrice = if (maxPriceSlider >= 150000f) null else maxPriceSlider.toDouble()
                            )
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("apply_filters_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply Filters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
