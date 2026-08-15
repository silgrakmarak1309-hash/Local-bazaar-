package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToListing: (String) -> Unit,
    onNavigateToSafety: () -> Unit,
    onNavigateToAdminModeration: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val myListings by viewModel.myListings.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: My Ads, 1: Favourites, 2: Business & Trust, 3: Settings
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showBoostSuccessDialog by remember { mutableStateOf(false) }
    var showAuthSheet by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val activeListings = remember(myListings) { myListings.filter { it.status == ListingStatus.ACTIVE } }
    val pendingListings = remember(myListings) { myListings.filter { it.status == ListingStatus.PENDING_REVIEW } }
    val soldListings = remember(myListings) { myListings.filter { it.status == ListingStatus.SOLD } }
    val draftListings = remember(myListings) { myListings.filter { it.status == ListingStatus.DRAFT } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    if (currentUser.isAdmin) {
                        IconButton(
                            onClick = onNavigateToAdminModeration,
                            modifier = Modifier.testTag("profile_admin_btn")
                        ) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Control Panel",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(
                        onClick = { showLogoutConfirmDialog = true },
                        modifier = Modifier.testTag("profile_top_logout_btn")
                    ) {
                        Icon(
                            Icons.Default.Logout,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
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
            // User Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentUser.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = currentUser.phoneNumber,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${currentUser.rating} (${currentUser.reviewCount} reviews)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier.testTag("edit_profile_btn")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VerifiedBadge(verification = currentUser.verificationBadge)
                            Text(
                                text = "Member since ${currentUser.joinedDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (currentUser.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentUser.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Account Actions Row (Switch Account & Log Out)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showAuthSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("login_switch_account_btn")
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch User", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showLogoutConfirmDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("profile_card_logout_btn")
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Log Out", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Prominent Admin Banner (Always visible for Admin users)
            if (currentUser.isAdmin) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToAdminModeration)
                            .testTag("admin_moderation_card_prominent"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Admin Control Panel",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = "SUPER ADMIN",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap to open moderation: Users, Block list, Ads approval & WhatsApp tools",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Tabs Selector
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("My Ads (${myListings.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_my_ads")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Saved (${favorites.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_favorites")
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Trust & Shop", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_business")
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Settings", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tab_settings")
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // My Ads Tab
                    if (myListings.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Outlined.Inventory2,
                                title = "You haven't posted any ads yet",
                                message = "Sell unused items or advertise your local service to buyers nearby.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        // Summary status chips
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = {},
                                    label = { Text("Active (${activeListings.size})") }
                                )
                                if (pendingListings.isNotEmpty()) {
                                    FilterChip(
                                        selected = false,
                                        onClick = {},
                                        label = { Text("Pending Review (${pendingListings.size})") }
                                    )
                                }
                                if (soldListings.isNotEmpty()) {
                                    FilterChip(
                                        selected = false,
                                        onClick = {},
                                        label = { Text("Sold (${soldListings.size})") }
                                    )
                                }
                            }
                        }

                        items(myListings) { item ->
                            MyListingManagementCard(
                                listing = item,
                                onClick = { onNavigateToListing(item.id) },
                                onMarkAsSold = { viewModel.markAsSold(item.id) },
                                onDelete = { viewModel.deleteListing(item.id) },
                                onBoost = {
                                    viewModel.toggleBoost(item.id, !item.isBoosted)
                                    if (!item.isBoosted) showBoostSuccessDialog = true
                                }
                            )
                        }
                    }
                }

                1 -> {
                    // Saved Favourites Tab
                    if (favorites.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Outlined.FavoriteBorder,
                                title = "No saved listings",
                                message = "Tap the heart icon on any product to save it to your wishlist and monitor price drops.",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(favorites) { fav ->
                            ProductCard(
                                listing = fav,
                                onClick = { onNavigateToListing(fav.id) },
                                onFavoriteToggle = { viewModel.toggleFavorite(fav.id) },
                                horizontalMode = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

                2 -> {
                    // Trust & Business Profile
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Local Business & Shop Verification",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Turn your profile into a verified local store on LocalBazaar to build instant community trust.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Business Mode", fontWeight = FontWeight.Bold)
                                        Text("Show badge on all listings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Switch(
                                        checked = currentUser.isBusinessAccount,
                                        onCheckedChange = { isBus ->
                                            viewModel.updateProfile(
                                                name = currentUser.name,
                                                bio = currentUser.bio,
                                                state = currentUser.state,
                                                district = currentUser.district,
                                                area = currentUser.area,
                                                isBusiness = isBus,
                                                businessName = if (isBus) (currentUser.businessName ?: "${currentUser.name}'s Bazaar") else null,
                                                businessCat = if (isBus) (currentUser.businessCategory ?: "Retail Store") else null
                                            )
                                        },
                                        modifier = Modifier.testTag("business_mode_switch")
                                    )
                                }

                                if (currentUser.isBusinessAccount) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Store Name: ${currentUser.businessName ?: currentUser.name}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Category: ${currentUser.businessCategory ?: "General Local Store"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Admin Moderation portal link
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = onNavigateToAdminModeration)
                                .testTag("admin_moderation_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Admin Moderation Portal",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Review submitted ads, approve listings & manage reports",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                3 -> {
                    // Settings Tab
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentUser.isAdmin) {
                                SettingsOptionRow(
                                    icon = Icons.Default.AdminPanelSettings,
                                    title = "Admin Moderation Portal",
                                    subtitle = "Full control panel: users, ad approvals, blocks & WhatsApp",
                                    onClick = onNavigateToAdminModeration
                                )
                            }

                            SettingsOptionRow(
                                icon = Icons.Default.Chat,
                                title = "WhatsApp Support & Helpdesk",
                                subtitle = "Chat directly with LocalBazaar support on WhatsApp",
                                onClick = {
                                    com.example.util.WhatsAppHelper.openWhatsAppChat(
                                        context = context,
                                        phoneNumber = "+91 98765 43210",
                                        message = "Hello LocalBazaar Support, I need help with my account (${currentUser.name}, Phone: ${currentUser.phone})."
                                    )
                                }
                            )

                            SettingsOptionRow(
                                icon = Icons.Default.Shield,
                                title = "Safety Center & Fraud Guide",
                                subtitle = "How to trade safely without scams",
                                onClick = onNavigateToSafety
                            )

                            SettingsOptionRow(
                                icon = Icons.Default.Notifications,
                                title = "Notification Center",
                                subtitle = "Order alerts, buyer chats & updates",
                                onClick = onNavigateToNotifications
                            )

                            SettingsOptionRow(
                                icon = Icons.Default.LocationOn,
                                title = "Current Location",
                                subtitle = "${selectedLocation.area}, ${selectedLocation.district}, ${selectedLocation.state}",
                                onClick = { showEditProfileDialog = true }
                            )

                            SettingsOptionRow(
                                icon = Icons.Default.Info,
                                title = "About LocalBazaar",
                                subtitle = "Version 1.0.0 • India-First Hyperlocal Marketplace",
                                onClick = {}
                            )

                            SettingsOptionRow(
                                icon = Icons.Default.Logout,
                                title = "Log Out of Account",
                                subtitle = "Sign out and return to the Login / Welcome screen",
                                onClick = { showLogoutConfirmDialog = true }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Button(
                                onClick = { showLogoutConfirmDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_logout_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                )
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log Out (${currentUser.name})", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showDeleteAccountDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("delete_account_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Delete Account & Clear Data")
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(currentUser.name) }
        var editBio by remember { mutableStateOf(currentUser.bio) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("About You / Store Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(
                            name = editName,
                            bio = editBio,
                            state = currentUser.state,
                            district = currentUser.district,
                            area = currentUser.area,
                            isBusiness = currentUser.isBusinessAccount,
                            businessName = currentUser.businessName,
                            businessCat = currentUser.businessCategory
                        )
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Boost Success Dialog
    if (showBoostSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showBoostSuccessDialog = false },
            title = { Text("🚀 Ad Promoted!", fontWeight = FontWeight.Bold) },
            text = { Text("Your listing has been featured at the top of search results in ${selectedLocation.district} for higher visibility.") },
            confirmButton = {
                Button(onClick = { showBoostSuccessDialog = false }) {
                    Text("Great!")
                }
            }
        )
    }

    // Log Out Confirmation Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Log Out?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of ${currentUser.name}? You will be returned to the Welcome/Login screen.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        viewModel.signOut(context)
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Account Confirmation
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete LocalBazaar Account?", fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove your active listings, conversation history, and profile data from this device.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.signOut(context)
                        showDeleteAccountDialog = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAuthSheet) {
        AuthBottomSheet(
            onDismiss = { showAuthSheet = false },
            onAuthSuccess = { newProfile ->
                viewModel.setUserProfile(newProfile)
                showAuthSheet = false
            }
        )
    }
}

@Composable
fun MyListingManagementCard(
    listing: ListingItem,
    onClick: () -> Unit,
    onMarkAsSold: () -> Unit,
    onDelete: () -> Unit,
    onBoost: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("my_listing_${listing.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    ProductImageThumbnail(listing = listing, modifier = Modifier.fillMaxSize())
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = listing.formattedPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (listing.status) {
                                ListingStatus.ACTIVE -> VerifiedGreenContainer
                                ListingStatus.PENDING_REVIEW -> Color(0xFFFEF3C7)
                                ListingStatus.SOLD -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = listing.status.name.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (listing.status) {
                                    ListingStatus.ACTIVE -> VerifiedGreen
                                    ListingStatus.PENDING_REVIEW -> Color(0xFFB45309)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${listing.viewsCount} views • ${listing.favoritesCount} favorites",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (listing.status == ListingStatus.ACTIVE) {
                    FilledTonalButton(
                        onClick = onBoost,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = BoostGold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (listing.isBoosted) "Boosted" else "Boost", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onMarkAsSold,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Mark Sold", fontSize = 12.sp)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun SettingsOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
