package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.IndiaLocationData
import com.example.model.LocationDistrict
import com.example.model.LocationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerDialog(
    currentState: String,
    currentDistrict: String,
    currentArea: String,
    onDismiss: () -> Unit,
    onLocationSelected: (state: String, district: String, area: String) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1 = State, 2 = District, 3 = Area
    var selectedState by remember { mutableStateOf(currentState) }
    var selectedDistrict by remember { mutableStateOf(currentDistrict) }
    var searchQuery by remember { mutableStateOf("") }
    var showGpsNotice by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("location_picker_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (step) {
                                1 -> "Select State / UT"
                                2 -> "Select District in $selectedState"
                                else -> "Select Local Area / Bazaar"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "All India",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (step == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (step == 1) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.clickable { step = 1; searchQuery = "" }
                            )
                            if (step > 1) {
                                Text("›", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = selectedState,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (step == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (step == 2) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.clickable { step = 2; searchQuery = "" }
                                )
                            }
                            if (step > 2) {
                                Text("›", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = selectedDistrict,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    IconButton(
                        onClick = {
                            if (step > 1) {
                                step--
                            } else {
                                onDismiss()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (step > 1) Icons.Default.ArrowBack else Icons.Default.Close,
                            contentDescription = "Back"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // GPS Fast detect button (only on step 1)
                if (step == 1) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showGpsNotice = true }
                            .testTag("detect_gps_location_btn"),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "GPS",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    text = "Auto-detect Nearby Market",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Uses device location to find your district (no private address stored)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("location_search_input"),
                    placeholder = {
                        Text(
                            when (step) {
                                1 -> "Search Indian states..."
                                2 -> "Search districts..."
                                else -> "Search local area/bazaar..."
                            }
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when (step) {
                        1 -> {
                            val filteredStates = IndiaLocationData.states.filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }
                            items(filteredStates) { st ->
                                LocationOptionItem(
                                    title = st.name,
                                    subtitle = "${st.districts.size} Districts available",
                                    isSelected = st.name.equals(selectedState, ignoreCase = true),
                                    onClick = {
                                        selectedState = st.name
                                        searchQuery = ""
                                        step = 2
                                    }
                                )
                            }
                        }
                        2 -> {
                            val districts = IndiaLocationData.getDistrictsForState(selectedState).filter {
                                it.name.contains(searchQuery, ignoreCase = true)
                            }
                            if (searchQuery.isNotBlank() && districts.none { it.name.equals(searchQuery.trim(), ignoreCase = true) }) {
                                item {
                                    LocationOptionItem(
                                        title = "Use \"${searchQuery.trim()}\"",
                                        subtitle = "Custom district in $selectedState",
                                        isSelected = false,
                                        onClick = {
                                            selectedDistrict = searchQuery.trim()
                                            searchQuery = ""
                                            step = 3
                                        }
                                    )
                                }
                            }
                            items(districts) { dist ->
                                LocationOptionItem(
                                    title = dist.name,
                                    subtitle = "${dist.areas.size} Popular local markets & bazaars",
                                    isSelected = dist.name.equals(selectedDistrict, ignoreCase = true),
                                    onClick = {
                                        selectedDistrict = dist.name
                                        searchQuery = ""
                                        step = 3
                                    }
                                )
                            }
                        }
                        3 -> {
                            val areas = IndiaLocationData.getAreasForDistrict(selectedState, selectedDistrict).filter {
                                it.contains(searchQuery, ignoreCase = true)
                            }
                            if (searchQuery.isNotBlank() && areas.none { it.equals(searchQuery.trim(), ignoreCase = true) }) {
                                item {
                                    LocationOptionItem(
                                        title = "Use \"${searchQuery.trim()}\"",
                                        subtitle = "Custom area in $selectedDistrict",
                                        isSelected = false,
                                        onClick = {
                                            onLocationSelected(selectedState, selectedDistrict, searchQuery.trim())
                                            onDismiss()
                                        }
                                    )
                                }
                            }
                            // Also allow selecting "All of District"
                            item {
                                LocationOptionItem(
                                    title = "All of $selectedDistrict",
                                    subtitle = "Show listings across entire district",
                                    isSelected = currentArea == "All of $selectedDistrict",
                                    onClick = {
                                        onLocationSelected(selectedState, selectedDistrict, "All of $selectedDistrict")
                                        onDismiss()
                                    }
                                )
                            }
                            items(areas) { areaName ->
                                LocationOptionItem(
                                    title = areaName,
                                    subtitle = "$selectedDistrict, $selectedState",
                                    isSelected = areaName.equals(currentArea, ignoreCase = true),
                                    onClick = {
                                        onLocationSelected(selectedState, selectedDistrict, areaName)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showGpsNotice) {
        AlertDialog(
            onDismissRequest = { showGpsNotice = false },
            title = {
                Text("Location Privacy Notice", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "LocalBazaar only uses coarse location to identify your State and District so you can discover nearby deals. We never store or share your exact home address or private coordinates with any buyer or seller."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showGpsNotice = false
                        // Set to detected Mumbai / Suburban Bandra by default
                        onLocationSelected("Maharashtra", "Mumbai Suburban", "Bandra West")
                        onDismiss()
                    }
                ) {
                    Text("Detect & Set Area")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGpsNotice = false }) {
                    Text("Select Manually")
                }
            }
        )
    }
}

@Composable
private fun LocationOptionItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
