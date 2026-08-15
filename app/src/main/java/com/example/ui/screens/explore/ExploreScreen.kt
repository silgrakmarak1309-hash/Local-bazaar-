package com.example.ui.screens.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ListingItem
import com.example.model.LocalBazaarCategories
import com.example.ui.components.*
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToListing: (String) -> Unit,
    onOpenChat: (ListingItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val listings by viewModel.listings.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    var showLocationPicker by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }
    var searchKeyword by remember { mutableStateOf(currentFilter.keyword) }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Header title + Location Breadcrumb
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "District Marketplace",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Interactive Breadcrumb
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { showLocationPicker = true }
                                    .testTag("explore_location_breadcrumb")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${selectedLocation.state} → ${selectedLocation.district} → ${selectedLocation.area}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Change",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // View mode toggle (Grid vs List)
                        IconButton(
                            onClick = { isGridView = !isGridView },
                            modifier = Modifier.testTag("view_mode_toggle")
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.ViewAgenda else Icons.Default.GridView,
                                contentDescription = "Toggle View"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search and Filter Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchKeyword,
                            onValueChange = {
                                searchKeyword = it
                                viewModel.setKeywordSearch(it)
                            },
                            placeholder = { Text("Search listings, brands, services...") },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                if (searchKeyword.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            searchKeyword = ""
                                            viewModel.setKeywordSearch("")
                                        }
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("explore_search_bar"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        FilledTonalIconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .size(50.dp)
                                .testTag("explore_filter_sheet_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = "Filters")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filter Pills
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = currentFilter.category == null && currentFilter.isService == null,
                                onClick = { viewModel.setCategoryFilter(null, null) },
                                label = { Text("All Items") },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("filter_chip_all")
                            )
                        }
                        items(LocalBazaarCategories.allCategories) { cat ->
                            val isSelected = currentFilter.category == cat.name
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        viewModel.setCategoryFilter(null, null)
                                    } else {
                                        viewModel.setCategoryFilter(cat.name, cat.isService)
                                    }
                                },
                                label = { Text(cat.name) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("filter_chip_${cat.id}")
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Active Results Summary & Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${listings.size} items found in ${selectedLocation.district}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (currentFilter.category != null || currentFilter.verifiedOnly || currentFilter.maxPrice != null || currentFilter.keyword.isNotBlank()) {
                    TextButton(
                        onClick = { viewModel.clearFilters() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Clear Filters",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (listings.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Outlined.SearchOff,
                    title = "No listings match your criteria",
                    message = "Try changing your search terms, removing filters, or broadening your district selection.",
                    actionLabel = "Reset Filters",
                    onAction = {
                        searchKeyword = ""
                        viewModel.clearFilters()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                            .testTag("explore_grid_view"),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(listings) { item ->
                            ProductCard(
                                listing = item,
                                onClick = { onNavigateToListing(item.id) },
                                onFavoriteToggle = { viewModel.toggleFavorite(item.id) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .testTag("explore_list_view"),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(listings) { item ->
                            if (item.isService) {
                                ServiceCard(
                                    listing = item,
                                    onClick = { onNavigateToListing(item.id) },
                                    onChatClick = { onOpenChat(item) }
                                )
                            } else {
                                ProductCard(
                                    listing = item,
                                    onClick = { onNavigateToListing(item.id) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                                    horizontalMode = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLocationPicker) {
        LocationPickerDialog(
            currentState = selectedLocation.state,
            currentDistrict = selectedLocation.district,
            currentArea = selectedLocation.area,
            onDismiss = { showLocationPicker = false },
            onLocationSelected = { st, dist, ar ->
                viewModel.setLocation(st, dist, ar)
            }
        )
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = currentFilter,
            onDismiss = { showFilterSheet = false },
            onApplyFilter = { newFilter ->
                viewModel.setFilter(newFilter)
            },
            onReset = {
                viewModel.clearFilters()
            }
        )
    }
}
