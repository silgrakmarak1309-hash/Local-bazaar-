package com.example.ui.screens.details

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.ListingItem
import com.example.model.SellerVerification
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingDetailScreen(
    listingId: String,
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onOpenChat: (ListingItem) -> Unit,
    onNavigateToListing: (String) -> Unit,
    onNavigateToSafety: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listingFlow = remember(listingId) { viewModel.getListingById(listingId) }
    val listing by listingFlow.collectAsState(initial = null)
    val context = LocalContext.current

    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("Fraud / Scam Suspicion") }
    var reportDetails by remember { mutableStateOf("") }
    var showReportSuccess by remember { mutableStateOf(false) }
    var showBlockConfirm by remember { mutableStateOf(false) }

    if (listing == null) {
        var isTimedOut by remember { mutableStateOf(false) }
        LaunchedEffect(listingId) {
            kotlinx.coroutines.delay(2000)
            isTimedOut = true
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Listing Details") },
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            modifier = modifier.fillMaxSize()
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (!isTimedOut) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading listing details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Listing Not Found",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "This listing may have been sold, deleted, or is unavailable in the database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onBack) {
                            Text("Back to Marketplace")
                        }
                    }
                }
            }
        }
        return
    }

    val item = listing!!
    val sellerListingsFlow = remember(item.sellerId) { viewModel.getSellerListings(item.sellerId) }
    val sellerOtherListings by sellerListingsFlow.collectAsState(initial = emptyList())
    val filteredSellerListings = remember(sellerOtherListings, item.id) {
        sellerOtherListings.filter { it.id != item.id }
    }

    val formattedDate = remember(item.postedTimestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        sdf.format(Date(item.postedTimestamp))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item.category, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, item.title)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Check out \"${item.title}\" for ${item.formattedPrice} in ${item.area}, ${item.district} on LocalBazaar!"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Listing"))
                        },
                        modifier = Modifier.testTag("detail_share_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }

                    IconButton(
                        onClick = { viewModel.toggleFavorite(item.id) },
                        modifier = Modifier.testTag("detail_fav_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) Color(0xFFF43F5E) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Report Listing") },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showReportDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Block Seller") },
                            leadingIcon = { Icon(Icons.Default.Block, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showBlockConfirm = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Safety Tips") },
                            leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                onNavigateToSafety()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.formattedPrice,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (item.isNegotiable) {
                            Text(
                                text = "Price Negotiable",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Button(
                        onClick = {
                            com.example.util.WhatsAppHelper.openWhatsAppChat(
                                context = context,
                                phoneNumber = com.example.util.WhatsAppHelper.getPhoneForSeller(item.sellerId),
                                message = "Hello ${item.sellerName}, I am interested in your listing \"${item.title}\" (${item.formattedPrice}) on LocalBazaar."
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("whatsapp_seller_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = com.example.util.WhatsAppGreenColor)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WhatsApp",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = { onOpenChat(item) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("chat_with_seller_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (item.isService) "Book" else "Chat",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
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
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Gallery Pager
            item {
                val imageList = if (item.images.isEmpty()) listOf("localbazaar_hero") else item.images
                val pagerState = rememberPagerState(pageCount = { imageList.size })

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Color.Black)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val img = imageList[page]
                        if (img.startsWith("http") || img.startsWith("content://") || img.startsWith("file://")) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(img).crossfade(true).build(),
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            val resId = if (img == "localbazaar_logo") R.drawable.localbazaar_logo else R.drawable.localbazaar_hero
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = item.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    // Pager indicator
                    if (imageList.size > 1) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1} / ${imageList.size}",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Main Info Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.formattedPrice,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        ConditionBadge(condition = item.condition)
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Location pill & Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.fullLocationDisplay,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Posted: $formattedDate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Safety Warning Box
            item {
                SafetyNoticeBanner(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onReadMore = onNavigateToSafety
                )
            }

            // Description Box
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
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // Seller Profile Section
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
                            text = if (item.isService) "Service Provider" else "Seller Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.sellerName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

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
                                        text = "${item.sellerRating} (${item.sellerReviewCount} reviews)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        VerifiedBadge(verification = item.sellerBadge)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                com.example.util.WhatsAppHelper.openWhatsAppChat(
                                    context = context,
                                    phoneNumber = com.example.util.WhatsAppHelper.getPhoneForSeller(item.sellerId),
                                    message = "Hello ${item.sellerName}, I am contacting you from LocalBazaar regarding your product \"${item.title}\" (₹${item.formattedPrice})."
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = com.example.util.WhatsAppGreenColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Contact Seller on WhatsApp",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "🛡️ Private Address Protected: LocalBazaar never exposes exact house numbers or personal identity documents.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Seller's Other Items
            if (filteredSellerListings.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SectionHeader(
                            title = "More from ${item.sellerName}",
                            subtitle = "Other verified listings"
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredSellerListings) { other ->
                                ProductCard(
                                    listing = other,
                                    onClick = { onNavigateToListing(other.id) },
                                    onFavoriteToggle = { viewModel.toggleFavorite(other.id) },
                                    modifier = Modifier.width(180.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Report Dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Report Listing", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a reason for reporting this listing:")
                    val reasons = listOf(
                        "Fraud / Scam Suspicion",
                        "Prohibited / Illegal Item",
                        "Fake or Counterfeit Product",
                        "Incorrect Price or Misleading Info",
                        "Harassment or Abusive Behavior"
                    )
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reportReason = r }
                        ) {
                            RadioButton(selected = reportReason == r, onClick = { reportReason = r })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(r, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        placeholder = { Text("Additional details (optional)...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitSafetyReport("Listing", item.id, reportReason, reportDetails) {
                            showReportDialog = false
                            showReportSuccess = true
                        }
                    }
                ) {
                    Text("Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showReportSuccess) {
        AlertDialog(
            onDismissRequest = { showReportSuccess = false },
            title = { Text("Report Received", fontWeight = FontWeight.Bold) },
            text = { Text("Thank you for helping keep LocalBazaar safe. Our safety moderation team will investigate this listing immediately.") },
            confirmButton = {
                Button(onClick = { showReportSuccess = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showBlockConfirm) {
        AlertDialog(
            onDismissRequest = { showBlockConfirm = false },
            title = { Text("Block Seller?", fontWeight = FontWeight.Bold) },
            text = { Text("You will no longer see listings or receive messages from ${item.sellerName}.") },
            confirmButton = {
                Button(
                    onClick = {
                        showBlockConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Block")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
