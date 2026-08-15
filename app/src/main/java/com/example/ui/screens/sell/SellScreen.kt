package com.example.ui.screens.sell

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.*
import com.example.ui.components.LocationPickerDialog
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.util.FirebaseStorageManager
import com.example.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellScreen(
    viewModel: MarketplaceViewModel,
    onListingCreated: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    var currentStep by remember { mutableStateOf(1) } // 1: Type, 2: Photos, 3: Details, 4: Preview, 5: Submitted

    // Form fields
    var isService by remember { mutableStateOf(false) }
    var uploadedImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Mobiles & Tablets") }
    var priceText by remember { mutableStateOf("") }
    var isNegotiable by remember { mutableStateOf(true) }
    var selectedCondition by remember { mutableStateOf(ItemCondition.GOOD) }
    var description by remember { mutableStateOf("") }
    var state by remember { mutableStateOf(selectedLocation.state) }
    var district by remember { mutableStateOf(selectedLocation.district) }
    var area by remember { mutableStateOf(selectedLocation.area) }

    var showLocationPicker by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var uploadStatusMessage by remember { mutableStateOf<String?>(null) }

    var replaceTargetIndex by remember { mutableStateOf<Int?>(null) }

    // Modern Android Photo Picker (Multi-image, up to 8 images)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val validUris = mutableListOf<String>()
            var lastErr: String? = null
            for (uri in uris) {
                val cachedUri = FirebaseStorageManager.cacheImageUri(context, uri)
                val validation = FirebaseStorageManager.validateImage(context, cachedUri)
                if (validation.isSuccess) {
                    validUris.add(cachedUri.toString())
                } else {
                    lastErr = validation.exceptionOrNull()?.message ?: "Invalid image selected"
                }
            }
            if (validUris.isNotEmpty()) {
                val currentNonSample = uploadedImages.filter { it != "localbazaar_hero" && it != "localbazaar_logo" }
                val combined = (currentNonSample + validUris).distinct().take(8)
                uploadedImages = combined
                validationError = lastErr
            } else if (lastErr != null) {
                validationError = lastErr
            }
        }
    }

    // Fallback Image Picker Launcher
    val fallbackPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val validUris = mutableListOf<String>()
            var lastErr: String? = null
            for (uri in uris) {
                val cachedUri = FirebaseStorageManager.cacheImageUri(context, uri)
                val validation = FirebaseStorageManager.validateImage(context, cachedUri)
                if (validation.isSuccess) {
                    validUris.add(cachedUri.toString())
                } else {
                    lastErr = validation.exceptionOrNull()?.message ?: "Invalid image selected"
                }
            }
            if (validUris.isNotEmpty()) {
                val currentNonSample = uploadedImages.filter { it != "localbazaar_hero" && it != "localbazaar_logo" }
                val combined = (currentNonSample + validUris).distinct().take(8)
                uploadedImages = combined
                validationError = lastErr
            } else if (lastErr != null) {
                validationError = lastErr
            }
        }
    }

    // Single image replacement launcher
    val replaceImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val cachedUri = FirebaseStorageManager.cacheImageUri(context, it)
            val validation = FirebaseStorageManager.validateImage(context, cachedUri)
            if (validation.isSuccess) {
                replaceTargetIndex?.let { index ->
                    if (index in uploadedImages.indices) {
                        val mutable = uploadedImages.toMutableList()
                        mutable[index] = cachedUri.toString()
                        uploadedImages = mutable
                    }
                }
            } else {
                validationError = validation.exceptionOrNull()?.message ?: "Invalid image selected"
            }
        }
        replaceTargetIndex = null
    }

    fun openImagePicker() {
        validationError = null
        try {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (_: Exception) {
            try {
                fallbackPickerLauncher.launch("image/*")
            } catch (e2: Exception) {
                validationError = "Unable to open image picker: ${e2.localizedMessage}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentStep) {
                            1 -> "Sell on LocalBazaar (Step 1/4)"
                            2 -> "Upload Photos (Step 2/4)"
                            3 -> "Listing Details (Step 3/4)"
                            4 -> "Preview & Submit (Step 4/4)"
                            else -> "Submission Status"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (currentStep > 1 && currentStep < 5) {
                                currentStep--
                            } else {
                                onCancel()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (currentStep in 2..4) Icons.Default.ArrowBack else Icons.Default.Close,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (currentStep in 1..3) {
                        TextButton(
                            onClick = {
                                // Save as Draft
                                val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                                val draftImages = if (uploadedImages.isEmpty()) listOf("localbazaar_hero") else uploadedImages
                                viewModel.createListing(
                                    title = if (title.isBlank()) "Draft Listing" else title,
                                    description = description,
                                    price = parsedPrice,
                                    isNegotiable = isNegotiable,
                                    category = selectedCategory,
                                    subcategory = "",
                                    isService = isService,
                                    images = draftImages,
                                    condition = if (isService) ItemCondition.NOT_APPLICABLE else selectedCondition,
                                    state = state,
                                    district = district,
                                    area = area,
                                    isDraft = true
                                ) { draftId ->
                                    onListingCreated(draftId)
                                }
                            },
                            modifier = Modifier.testTag("save_draft_btn")
                        ) {
                            Text("Save Draft", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentStep in 1..4) {
                Surface(
                    tonalElevation = 6.dp,
                    shadowElevation = 6.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isSubmitting && uploadStatusMessage != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = uploadStatusMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (currentStep > 1) {
                                OutlinedButton(
                                    onClick = { currentStep-- },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f),
                                    enabled = !isSubmitting
                                ) {
                                    Text("Back")
                                }
                            }

                            Button(
                                onClick = {
                                    validationError = null
                                    when (currentStep) {
                                        1 -> currentStep = 2
                                        2 -> {
                                            if (uploadedImages.isEmpty()) {
                                                validationError = "Please add at least 1 photo of your item to proceed."
                                            } else {
                                                currentStep = 3
                                            }
                                        }
                                        3 -> {
                                            val price = priceText.toDoubleOrNull()
                                            if (title.trim().length < 5) {
                                                validationError = "Title must be at least 5 characters long"
                                            } else if (price == null || price <= 0) {
                                                validationError = "Please enter a valid price"
                                            } else if (description.trim().length < 10) {
                                                validationError = "Description must be at least 10 characters long"
                                            } else {
                                                currentStep = 4
                                            }
                                        }
                                        4 -> {
                                            // Upload images to Firebase Storage securely, then create listing
                                            coroutineScope.launch {
                                                isSubmitting = true
                                                uploadStatusMessage = "Preparing images for Firebase Storage..."
                                                try {
                                                    val tempListingId = "lst_" + UUID.randomUUID().toString().take(8)
                                                    
                                                    // Upload all selected gallery images to Firebase Storage
                                                    val finalUrls = FirebaseStorageManager.uploadAllListingImages(
                                                        context = context,
                                                        userId = currentUser.id,
                                                        listingId = tempListingId,
                                                        images = if (uploadedImages.isEmpty()) listOf("localbazaar_hero") else uploadedImages
                                                    ) { current, total ->
                                                        uploadStatusMessage = "Uploading photo $current of $total to Firebase Storage..."
                                                    }

                                                    uploadStatusMessage = "Finalizing listing submission..."
                                                    val price = priceText.toDoubleOrNull() ?: 0.0
                                                    viewModel.createListing(
                                                        title = title,
                                                        description = description,
                                                        price = price,
                                                        isNegotiable = isNegotiable,
                                                        category = selectedCategory,
                                                        subcategory = "",
                                                        isService = isService,
                                                        images = finalUrls,
                                                        condition = if (isService) ItemCondition.NOT_APPLICABLE else selectedCondition,
                                                        state = state,
                                                        district = district,
                                                        area = area,
                                                        isDraft = false
                                                    ) { _ ->
                                                        isSubmitting = false
                                                        uploadStatusMessage = null
                                                        currentStep = 5
                                                    }
                                                } catch (e: Exception) {
                                                    isSubmitting = false
                                                    uploadStatusMessage = null
                                                    validationError = e.localizedMessage ?: "Image upload to Firebase Storage failed. Please check network connection and try again."
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(if (currentStep > 1) 1.5f else 1f)
                                    .testTag("sell_next_btn"),
                                enabled = !isSubmitting
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                } else {
                                    Text(
                                        text = if (currentStep == 4) "Submit for Review" else "Next Step →",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = { currentStep / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Validation error alert
            if (validationError != null) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = validationError!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            when (currentStep) {
                1 -> {
                    // Step 1: Choose Type (Product vs Service)
                    Text(
                        text = "What would you like to list?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Select whether you are selling a physical product or offering a local service.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ListingTypeCard(
                        title = "📦 Physical Product",
                        description = "Smartphones, electronics, bikes, cars, furniture, appliances, books, fashion, and goods.",
                        isSelected = !isService,
                        onClick = {
                            isService = false
                            selectedCategory = "Mobiles & Tablets"
                        },
                        tag = "type_product"
                    )

                    ListingTypeCard(
                        title = "🛠️ Local Service or Business",
                        description = "Tutors, electricians, plumbers, mechanics, tailoring, cleaning, photography, carpentry.",
                        isSelected = isService,
                        onClick = {
                            isService = true
                            selectedCategory = "Tutors & Coaching"
                        },
                        tag = "type_service"
                    )
                }

                2 -> {
                    // Step 2: Upload Images (up to 8)
                    Text(
                        text = "Upload Photos (up to 8)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real photos of your actual product increase buyer trust and lead to 3x faster sales. Photos are securely stored on Firebase Storage.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Photo action row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { openImagePicker() },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pick_images_btn")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Photos from Gallery (${uploadedImages.size}/8)")
                        }
                    }

                    // If empty, show big upload box
                    if (uploadedImages.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { openImagePicker() }
                                .testTag("upload_placeholder_box"),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Upload Photos",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap here to select product photos from Gallery",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Supports JPEG, PNG, WebP • Up to 8 photos",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Thumbnails Grid / Row
                    if (uploadedImages.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${uploadedImages.size} of 8 photos selected",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            if (uploadedImages.size < 8) {
                                TextButton(
                                    onClick = { openImagePicker() },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add More", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(uploadedImages) { index, img ->
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            width = if (index == 0) 2.dp else 1.dp,
                                            color = if (index == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    if (img.startsWith("content://") || img.startsWith("http") || img.startsWith("file://")) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current).data(img).crossfade(true).build(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        val resId = if (img == "localbazaar_logo") R.drawable.localbazaar_logo else R.drawable.localbazaar_hero
                                        Image(
                                            painter = painterResource(id = resId),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    if (index == 0) {
                                        Surface(
                                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = "COVER",
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    // Bottom action overlay to replace
                                    Surface(
                                        shape = RoundedCornerShape(topStart = 8.dp),
                                        color = Color.Black.copy(alpha = 0.55f),
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .clickable {
                                                replaceTargetIndex = index
                                                try {
                                                    replaceImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                                } catch (_: Exception) {
                                                    openImagePicker()
                                                }
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Replace",
                                                tint = Color.White,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "Replace",
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Delete icon
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.65f))
                                            .clickable {
                                                uploadedImages = uploadedImages.filterIndexed { i, _ -> i != index }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Step 3: Enter Details
                    Text(
                        text = "Listing Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title / Headline *") },
                        placeholder = { Text("e.g. iPhone 14 Pro 128GB with box, or 24x7 Electrician") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_title_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category Dropdown
                    var catExpanded by remember { mutableStateOf(false) }
                    val categoriesList = if (isService) LocalBazaarCategories.serviceCategories else LocalBazaarCategories.productCategories

                    ExposedDropdownMenuBox(
                        expanded = catExpanded,
                        onExpandedChange = { catExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("sell_category_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            categoriesList.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.name) },
                                    onClick = {
                                        selectedCategory = cat.name
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Price
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text(if (isService) "Visiting / Starting Fee (₹) *" else "Price (₹) *") },
                        placeholder = { Text("e.g. 15000") },
                        leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(start = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_price_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Negotiable Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Price is Negotiable", fontWeight = FontWeight.Medium)
                        Switch(
                            checked = isNegotiable,
                            onCheckedChange = { isNegotiable = it },
                            modifier = Modifier.testTag("sell_negotiable_switch")
                        )
                    }

                    // Condition (only for physical products)
                    if (!isService) {
                        Text("Condition", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                ItemCondition.BRAND_NEW,
                                ItemCondition.LIKE_NEW,
                                ItemCondition.GOOD,
                                ItemCondition.FAIR
                            ).forEach { cond ->
                                val isSelected = selectedCondition == cond
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCondition = cond },
                                    label = { Text(cond.label) },
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Detailed Description *") },
                        placeholder = { Text("Mention age, usage, accessories included, reason for selling...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("sell_desc_input"),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 6
                    )

                    // Location Selector Pill
                    Text("Listing Location", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showLocationPicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("$area, $district", fontWeight = FontWeight.Bold)
                                    Text(state, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("Change →", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                4 -> {
                    // Step 4: Preview
                    Text(
                        text = "Review Your Listing",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Here is how your listing will appear to buyers across $district.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val previewPrice = priceText.toDoubleOrNull() ?: 0.0
                    val displayImages = if (uploadedImages.isEmpty()) listOf("localbazaar_hero") else uploadedImages
                    val previewItem = ListingItem(
                        id = "preview_1",
                        title = title,
                        description = description,
                        price = previewPrice,
                        isNegotiable = isNegotiable,
                        category = selectedCategory,
                        isService = isService,
                        images = displayImages,
                        condition = if (isService) ItemCondition.NOT_APPLICABLE else selectedCondition,
                        state = state,
                        district = district,
                        area = area,
                        sellerId = currentUser.id,
                        sellerName = currentUser.name,
                        sellerBadge = currentUser.verificationBadge,
                        sellerRating = currentUser.rating,
                        sellerReviewCount = currentUser.reviewCount,
                        postedTimestamp = System.currentTimeMillis()
                    )

                    ProductCard(
                        listing = previewItem,
                        onClick = {},
                        onFavoriteToggle = {},
                        horizontalMode = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF), // Soft info blue
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D4ED8))
                            Text(
                                text = "Once submitted, photos will be securely uploaded to Firebase Storage and your listing will be verified by LocalBazaar automated moderation before going live.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }
                }

                5 -> {
                    // Step 5: Submitted Feedback
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(VerifiedGreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = VerifiedGreen,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Text(
                            text = "Submitted for Review!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Your listing \"$title\" and images have been uploaded. It is now in the Pending Review queue. You will receive an instant notification once approved.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = onCancel,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("done_sell_btn")
                        ) {
                            Text("Return to Marketplace", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showLocationPicker) {
        LocationPickerDialog(
            currentState = state,
            currentDistrict = district,
            currentArea = area,
            onDismiss = { showLocationPicker = false },
            onLocationSelected = { st, dist, ar ->
                state = st
                district = dist
                area = ar
            }
        )
    }
}

@Composable
private fun ListingTypeCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
            )
        } else {
            CardDefaults.outlinedCardBorder()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

