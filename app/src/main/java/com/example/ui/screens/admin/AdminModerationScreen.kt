package com.example.ui.screens.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminUserInfo
import com.example.model.ListingItem
import com.example.model.ListingStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductImageThumbnail
import com.example.ui.components.VerifiedBadge
import com.example.ui.theme.VerifiedGreen
import com.example.viewmodel.MarketplaceViewModel

private val WhatsAppGreen = Color(0xFF25D366)

fun launchWhatsApp(context: Context, phoneNumber: String, defaultMessage: String = "") {
    try {
        val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
        val finalPhone = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
        val encodedMsg = java.net.URLEncoder.encode(defaultMessage, "UTF-8")
        val url = if (finalPhone.isNotBlank()) {
            "https://api.whatsapp.com/send?phone=$finalPhone&text=$encodedMsg"
        } else {
            "https://api.whatsapp.com/send?text=$encodedMsg"
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "WhatsApp could not be opened. Please ensure WhatsApp is installed.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminModerationScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Users & Block", "Products (Sell/Unsell)", "Pending Queue")

    val pendingListings by viewModel.pendingReviewListings.collectAsState()
    val allListings by viewModel.allMarketplaceListings.collectAsState()
    val adminUsers by viewModel.adminUsers.collectAsState()
    val blockedUserIds by viewModel.blockedUserIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForReject by remember { mutableStateOf<ListingItem?>(null) }
    var rejectReason by remember { mutableStateOf("Prohibited Item / Policy Violation") }
    var listingStatusFilter by remember { mutableStateOf("All") }
    var showDirectWhatsAppDialog by remember { mutableStateOf(false) }
    var directWhatsAppPhone by remember { mutableStateOf("") }
    var directWhatsAppMessage by remember { mutableStateOf("Hello from LocalBazaar Admin.") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Admin Control Panel", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Moderate users, products, sell/unsell & WhatsApp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showDirectWhatsAppDialog = true },
                        modifier = Modifier.testTag("admin_direct_whatsapp_btn")
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "WhatsApp Launcher",
                            tint = WhatsAppGreen
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDirectWhatsAppDialog = true },
                containerColor = WhatsAppGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Chat, contentDescription = "WhatsApp") },
                text = { Text("WhatsApp Chat", fontWeight = FontWeight.Bold) },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("admin_fab_whatsapp")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Navigation Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal)
                                if (index == 2 && pendingListings.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                                        Text("${pendingListings.size}", color = MaterialTheme.colorScheme.onError)
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Search Bar for all tabs
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("admin_search_input"),
                placeholder = {
                    Text(
                        when (selectedTabIndex) {
                            0 -> "Search user by ID, Name or Phone..."
                            1 -> "Search product by title, seller or ID..."
                            else -> "Search pending review queue..."
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Main Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // TAB 0: Users & Block / Unblock / WhatsApp
                    val filteredUsers = remember(adminUsers, searchQuery, blockedUserIds) {
                        adminUsers.filter { user ->
                            val query = searchQuery.trim().lowercase()
                            query.isEmpty() ||
                                user.userId.lowercase().contains(query) ||
                                user.name.lowercase().contains(query) ||
                                user.phone.lowercase().contains(query) ||
                                user.location.lowercase().contains(query)
                        }
                    }

                    if (filteredUsers.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.PersonOff,
                            title = "No Users Found",
                            message = if (searchQuery.isEmpty()) "No user accounts registered yet." else "No users match '$searchQuery'."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Registered Users (${filteredUsers.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Blocked: ${blockedUserIds.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (blockedUserIds.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            items(filteredUsers, key = { it.userId }) { user ->
                                val isBlocked = blockedUserIds.contains(user.userId)
                                AdminUserCard(
                                    user = user.copy(isBlocked = isBlocked),
                                    onToggleBlock = {
                                        viewModel.toggleBlockUser(user.userId, !isBlocked)
                                        val msg = if (!isBlocked) "User ${user.name} has been BLOCKED" else "User ${user.name} has been UNBLOCKED"
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    },
                                    onWhatsAppClick = {
                                        launchWhatsApp(
                                            context = context,
                                            phoneNumber = user.phone,
                                            defaultMessage = "Hello ${user.name}, this is LocalBazaar Admin regarding your account (ID: ${user.userId})."
                                        )
                                    },
                                    allUserListings = allListings.filter { it.sellerId == user.userId },
                                    onToggleSellStatus = { listingId, currentStatus ->
                                        viewModel.toggleListingSellStatus(listingId, currentStatus)
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: Products (Sell / Unsell & WhatsApp)
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Filter chips for product status
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Active", "Sold", "Pending").forEach { filter ->
                                FilterChip(
                                    selected = listingStatusFilter == filter,
                                    onClick = { listingStatusFilter = filter },
                                    label = { Text(filter) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }

                        val filteredProducts = remember(allListings, searchQuery, listingStatusFilter) {
                            allListings.filter { item ->
                                val matchesSearch = searchQuery.isEmpty() ||
                                    item.title.contains(searchQuery, ignoreCase = true) ||
                                    item.sellerName.contains(searchQuery, ignoreCase = true) ||
                                    item.id.contains(searchQuery, ignoreCase = true) ||
                                    item.sellerId.contains(searchQuery, ignoreCase = true)

                                val matchesStatus = when (listingStatusFilter) {
                                    "Active" -> item.status == ListingStatus.ACTIVE
                                    "Sold" -> item.status == ListingStatus.SOLD
                                    "Pending" -> item.status == ListingStatus.PENDING_REVIEW
                                    else -> true
                                }
                                matchesSearch && matchesStatus
                            }
                        }

                        if (filteredProducts.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.Inventory2,
                                title = "No Products Found",
                                message = if (searchQuery.isEmpty()) "No marketplace listings found." else "No listings match '$searchQuery'."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(filteredProducts, key = { it.id }) { item ->
                                    val isBlockedSeller = blockedUserIds.contains(item.sellerId)
                                    AdminProductCard(
                                        item = item,
                                        isBlockedSeller = isBlockedSeller,
                                        onToggleSell = {
                                            viewModel.toggleListingSellStatus(item.id, item.status)
                                            val newStatusMsg = if (item.status == ListingStatus.ACTIVE) "Marked as SOLD (Unsold from market)" else "Marked as ACTIVE (Live for sale)"
                                            Toast.makeText(context, newStatusMsg, Toast.LENGTH_SHORT).show()
                                        },
                                        onWhatsAppSeller = {
                                            launchWhatsApp(
                                                context = context,
                                                phoneNumber = getSellerPhone(item.sellerId),
                                                defaultMessage = "Hello ${item.sellerName}, this is LocalBazaar Admin regarding your product \"${item.title}\" (₹${item.formattedPrice})."
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: Pending Queue
                    val filteredPending = remember(pendingListings, searchQuery) {
                        if (searchQuery.isBlank()) pendingListings
                        else pendingListings.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                                it.sellerName.contains(searchQuery, ignoreCase = true) ||
                                it.id.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredPending.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.CheckCircle,
                            title = "Moderation Queue is Clean",
                            message = "All user listings have been verified and approved. New submissions will show up here automatically."
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredPending, key = { it.id }) { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("moderation_card_${item.id}"),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                            ) {
                                                ProductImageThumbnail(listing = item, modifier = Modifier.fillMaxSize())
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.formattedPrice,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = item.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "Seller: ${item.sellerName} (ID: ${item.sellerId})",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Text(
                                                    text = item.locationDisplay,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = item.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 3
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Action buttons: WhatsApp, Reject, Approve
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    launchWhatsApp(
                                                        context = context,
                                                        phoneNumber = getSellerPhone(item.sellerId),
                                                        defaultMessage = "Hello ${item.sellerName}, LocalBazaar Admin reviewing your submission: \"${item.title}\"."
                                                    )
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WhatsAppGreen),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("WhatsApp", fontSize = 12.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { selectedItemForReject = item },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Reject", fontSize = 12.sp)
                                            }

                                            Button(
                                                onClick = {
                                                    viewModel.approveListing(item.id)
                                                    Toast.makeText(context, "Listing approved & live!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Approve", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedItemForReject != null) {
        AlertDialog(
            onDismissRequest = { selectedItemForReject = null },
            title = { Text("Reject Listing", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select reason for rejection:")
                    val reasons = listOf(
                        "Prohibited Item / Policy Violation",
                        "Misleading Price or Info",
                        "Inappropriate / Blurry Images",
                        "Spam or Duplicate Post"
                    )
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { rejectReason = r }
                        ) {
                            RadioButton(selected = rejectReason == r, onClick = { rejectReason = r })
                            Text(r, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rejectListing(selectedItemForReject!!.id, rejectReason)
                        selectedItemForReject = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForReject = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDirectWhatsAppDialog) {
        AlertDialog(
            onDismissRequest = { showDirectWhatsAppDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen)
                    Text("Open Direct WhatsApp Chat", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter any 10-digit mobile number to initiate an immediate WhatsApp chat:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = directWhatsAppPhone,
                        onValueChange = { directWhatsAppPhone = it },
                        label = { Text("Mobile Number / Phone") },
                        placeholder = { Text("e.g. 9876543210") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = directWhatsAppMessage,
                        onValueChange = { directWhatsAppMessage = it },
                        label = { Text("Default Message") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (directWhatsAppPhone.isNotBlank()) {
                            launchWhatsApp(
                                context = context,
                                phoneNumber = directWhatsAppPhone,
                                defaultMessage = directWhatsAppMessage
                            )
                            showDirectWhatsAppDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Launch WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDirectWhatsAppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminUserCard(
    user: AdminUserInfo,
    onToggleBlock: () -> Unit,
    onWhatsAppClick: () -> Unit,
    allUserListings: List<ListingItem>,
    onToggleSellStatus: (String, ListingStatus) -> Unit
) {
    var expandedProducts by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_user_card_${user.userId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBlocked) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (user.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = if (user.isBlocked) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 18.sp
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        VerifiedBadge(verification = user.verificationBadge)
                    }

                    // User ID display
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "UID: ${user.userId}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "${user.phone} • ${user.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Block / Active Status Badge
                Surface(
                    color = if (user.isBlocked) MaterialTheme.colorScheme.error else VerifiedGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (user.isBlocked) "BLOCKED" else "ACTIVE",
                        color = if (user.isBlocked) MaterialTheme.colorScheme.onError else VerifiedGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row: Block/Unblock & WhatsApp Chat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WhatsApp button
                Button(
                    onClick = onWhatsAppClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                // Block / Unblock button
                if (user.isBlocked) {
                    Button(
                        onClick = onToggleBlock,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unblock User", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onToggleBlock,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Block User", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Products dropdown toggle
            if (allUserListings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { expandedProducts = !expandedProducts },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (expandedProducts) "Hide Products (${allUserListings.size})" else "View Products & Sell/Unsell (${allUserListings.size})",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        if (expandedProducts) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = expandedProducts) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        allUserListings.forEach { listing ->
                            UserMiniListingItem(
                                listing = listing,
                                onToggleSell = { onToggleSellStatus(listing.id, listing.status) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserMiniListingItem(
    listing: ListingItem,
    onToggleSell: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                ProductImageThumbnail(listing = listing, modifier = Modifier.fillMaxSize())
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = listing.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${listing.formattedPrice} • Status: ${listing.status.label}",
                    fontSize = 11.sp,
                    color = if (listing.status == ListingStatus.ACTIVE) VerifiedGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Sell / Unsell button
            Button(
                onClick = onToggleSell,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (listing.status == ListingStatus.ACTIVE) MaterialTheme.colorScheme.error else VerifiedGreen
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (listing.status == ListingStatus.ACTIVE) "Unsell" else "Sell",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AdminProductCard(
    item: ListingItem,
    isBlockedSeller: Boolean,
    onToggleSell: () -> Unit,
    onWhatsAppSeller: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_product_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    ProductImageThumbnail(listing = item, modifier = Modifier.fillMaxSize())
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.formattedPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Status Badge
                        Surface(
                            color = when (item.status) {
                                ListingStatus.ACTIVE -> VerifiedGreen.copy(alpha = 0.15f)
                                ListingStatus.SOLD -> MaterialTheme.colorScheme.secondaryContainer
                                ListingStatus.PENDING_REVIEW -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.errorContainer
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = item.status.label,
                                color = when (item.status) {
                                    ListingStatus.ACTIVE -> VerifiedGreen
                                    ListingStatus.SOLD -> MaterialTheme.colorScheme.onSecondaryContainer
                                    ListingStatus.PENDING_REVIEW -> MaterialTheme.colorScheme.onTertiaryContainer
                                    else -> MaterialTheme.colorScheme.error
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Seller: ${item.sellerName} (ID: ${item.sellerId})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isBlockedSeller) {
                        Text(
                            text = "⚠ Seller account is currently BLOCKED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = item.locationDisplay,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: WhatsApp Seller + Sell / Unsell Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WhatsApp Button
                Button(
                    onClick = onWhatsAppSeller,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Sell / Unsell Button
                if (item.status == ListingStatus.ACTIVE) {
                    OutlinedButton(
                        onClick = onToggleSell,
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unsell (Mark Sold)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onToggleSell,
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = VerifiedGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sell (Mark Active)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun getSellerPhone(sellerId: String): String {
    return when (sellerId) {
        "usr_rohit_m" -> "+91 98201 45678"
        "usr_priya_s" -> "+91 98332 98765"
        "usr_amit_v" -> "+91 91672 34567"
        "usr_neha_k" -> "+91 99203 12345"
        "usr_vikram_p" -> "+91 98450 87654"
        "usr_ananya_b" -> "+91 97401 23456"
        "usr_rahul_d" -> "+91 98110 56789"
        "usr_pooja_n" -> "+91 99100 43210"
        else -> "+91 98765 43210"
    }
}
