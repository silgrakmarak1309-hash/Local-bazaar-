package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToListing: (String) -> Unit,
    onNavigateToExplore: (category: String?, isService: Boolean?) -> Unit,
    onNavigateToSell: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSafety: () -> Unit,
    onOpenChat: (ListingItem) -> Unit,
    onNavigateToAdminModeration: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val listings by viewModel.listings.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsState()
    var showLocationPicker by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val currentFilter by viewModel.currentFilter.collectAsState()

    var activeTabIsService by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }

    val featuredListings = remember(listings) {
        listings.filter { it.isFeatured || it.isBoosted }
    }

    val nearbyListings = remember(listings, selectedLocation) {
        listings.filter { !it.isService }
    }

    val localServices = remember(listings) {
        listings.filter { it.isService }
    }

    val localBusinesses = remember(listings) {
        listings.filter { it.sellerBadge == SellerVerification.VERIFIED_BUSINESS || it.businessName != null }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Brand Logo + Location Selector + Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Icon + Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { viewModel.clearFilters() }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.meri_local_bazaar_icon_1786755369661),
                                contentDescription = "Meri Local Bazaar Logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Column {
                                Text(
                                    text = "LocalBazaar",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    text = "Your Local Market, One App.",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (currentUser.isAdmin) {
                                IconButton(
                                    onClick = onNavigateToAdminModeration,
                                    modifier = Modifier.testTag("home_admin_panel_btn")
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = "Admin Panel",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Notification Icon with Badge
                            IconButton(
                                onClick = onNavigateToNotifications,
                                modifier = Modifier.testTag("notifications_btn")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotifications > 0) {
                                            Badge { Text("$unreadNotifications") }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location Selector Pill
                    LocationSelectorChip(
                        state = selectedLocation.state,
                        district = selectedLocation.district,
                        area = selectedLocation.area,
                        onClick = { showLocationPicker = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar with Filter Button
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
                            placeholder = {
                                Text(
                                    "Search mobiles, furniture, tutors in ${selectedLocation.area}...",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 13.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
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
                                .height(52.dp)
                                .testTag("home_search_bar"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                            )
                        )

                        FilledIconButton(
                            onClick = { showFilterSheet = true },
                            modifier = Modifier
                                .size(52.dp)
                                .testTag("home_filter_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Safety Banner
            item {
                SafetyNoticeBanner(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    onReadMore = onNavigateToSafety
                )
            }

            // Categories Section with Products / Services Toggle
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Browse Categories",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Toggle Products vs Services
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (!activeTabIsService) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { activeTabIsService = false }
                                        .testTag("tab_products")
                                ) {
                                    Text(
                                        text = "Products",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!activeTabIsService) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (activeTabIsService) MaterialTheme.colorScheme.secondary else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { activeTabIsService = true }
                                        .testTag("tab_services")
                                ) {
                                    Text(
                                        text = "Services",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (activeTabIsService) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val displayCategories = if (activeTabIsService) {
                        LocalBazaarCategories.serviceCategories
                    } else {
                        LocalBazaarCategories.productCategories
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayCategories) { cat ->
                            CategoryCardItem(
                                category = cat,
                                onClick = {
                                    onNavigateToExplore(cat.name, cat.isService)
                                }
                            )
                        }
                    }
                }
            }

            // Prominent "Sell Something" Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onNavigateToSell)
                        .testTag("home_sell_banner"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFE65100),
                                        Color(0xFFFF8F00),
                                        Color(0xFF00695C)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "FREE LOCAL LISTING",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Got something to sell or service to offer?",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Reach thousands of buyers in ${selectedLocation.district} in minutes.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = onNavigateToSell,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Post Free Ad",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // Featured & Promoted Section
            if (featuredListings.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "🔥 Featured & Promoted in ${selectedLocation.district}",
                            subtitle = "Verified top deals in your district",
                            actionLabel = "See All",
                            onActionClick = { onNavigateToExplore(null, null) }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(featuredListings) { item ->
                                ProductCard(
                                    listing = item,
                                    onClick = { onNavigateToListing(item.id) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                                    modifier = Modifier.width(200.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Nearby Listings in Selected Area
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(
                        title = "📍 Nearby in ${selectedLocation.area}",
                        subtitle = "Fresh items listed in your neighborhood",
                        actionLabel = "Explore All",
                        onActionClick = { onNavigateToExplore(null, false) }
                    )
                    if (nearbyListings.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Outlined.Storefront,
                            title = "No products found in this area",
                            message = "Be the first one in ${selectedLocation.area} to post a product, or switch your district to find more listings.",
                            actionLabel = "Post Item",
                            onAction = onNavigateToSell
                        )
                    } else {
                        // Display horizontal row or grid
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(nearbyListings) { item ->
                                ProductCard(
                                    listing = item,
                                    onClick = { onNavigateToListing(item.id) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                                    modifier = Modifier.width(180.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Top Local Services Section (Tutors, Electricians, Plumbers, Mechanics)
            if (localServices.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "🛠️ Top Local Services & Repairs",
                            subtitle = "Trusted technicians, tutors & experts in ${selectedLocation.district}",
                            actionLabel = "All Services",
                            onActionClick = { onNavigateToExplore(null, true) }
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            localServices.take(4).forEach { srv ->
                                ServiceCard(
                                    listing = srv,
                                    onClick = { onNavigateToListing(srv.id) },
                                    onChatClick = { onOpenChat(srv) }
                                )
                            }
                        }
                    }
                }
            }

            // Verified Local Businesses & Shops
            if (localBusinesses.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "🏢 Verified Local Shops & Businesses",
                            subtitle = "Discover verified shops in ${selectedLocation.district}",
                            actionLabel = "View Shops",
                            onActionClick = { onNavigateToExplore(null, null) }
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(localBusinesses) { bus ->
                                BusinessSpotlightCard(
                                    listing = bus,
                                    onClick = { onNavigateToListing(bus.id) }
                                )
                            }
                        }
                    }
                }
            }

            // Recently Added
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SectionHeader(
                        title = "⚡ Recently Added Listings",
                        subtitle = "Latest deals across ${selectedLocation.state}",
                        actionLabel = "View All",
                        onActionClick = { onNavigateToExplore(null, null) }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listings.take(6).forEach { item ->
                            ProductCard(
                                listing = item,
                                onClick = { onNavigateToListing(item.id) },
                                onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                                horizontalMode = true,
                                modifier = Modifier.fillMaxWidth()
                            )
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

@Composable
fun CategoryCardItem(
    category: CategoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .clickable(onClick = onClick)
            .testTag("cat_card_${category.id}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (category.isService) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(60.dp),
            tonalElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = if (category.isService) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BusinessSpotlightCard(
    listing: ListingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("business_spotlight_${listing.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BoostGoldContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = BoostGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                VerifiedBadge(verification = SellerVerification.VERIFIED_BUSINESS, compact = true)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = listing.businessName ?: listing.sellerName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = listing.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = listing.area,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "★ ${listing.sellerRating}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B)
                )
            }
        }
    }
}
