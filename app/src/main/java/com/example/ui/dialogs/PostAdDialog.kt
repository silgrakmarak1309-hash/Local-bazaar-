package com.example.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage

data class PhotoSlotItem(
  val label: String,
  val isAttached: Boolean = false,
  val uri: Uri? = null,
  val sampleEmoji: String = "📦",
  val sampleColor: Long = 0xFFDCFCE7
)

@Composable
fun PostAdDialog(
  onDismiss: () -> Unit,
  onPostListing: (
    title: String,
    category: String,
    price: Double,
    description: String,
    isService: Boolean,
    phoneOrWhatsapp: String
  ) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Mobile / Laptop") }
  var categoryMenuExpanded by remember { mutableStateOf(false) }
  var priceText by remember { mutableStateOf("") }
  var phoneOrWhatsapp by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var isService by remember { mutableStateOf(false) }

  // 4 Photo Slots: Initialized with 2 pre-attached photos for instant listing, and slots for 3-4 photos
  val photoSlots = remember {
    mutableStateListOf(
      PhotoSlotItem("Cover Photo", isAttached = true, sampleEmoji = "📸", sampleColor = 0xFFDCFCE7),
      PhotoSlotItem("Side Angle", isAttached = true, sampleEmoji = "🔍", sampleColor = 0xFFE0F2FE),
      PhotoSlotItem("Details/Bill", isAttached = false, sampleEmoji = "📄", sampleColor = 0xFFFEF3C7),
      PhotoSlotItem("Packaging", isAttached = false, sampleEmoji = "📦", sampleColor = 0xFFF3E8FF)
    )
  }

  var activeSlotIndex by remember { mutableStateOf<Int?>(null) }

  // Direct Gallery / Recent / Downloads image picker using GetContent
  val getContentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    val index = activeSlotIndex
    if (uri != null && index != null && index in 0 until photoSlots.size) {
      photoSlots[index] = photoSlots[index].copy(isAttached = true, uri = uri)
    }
  }

  // Multiple photos picker for Recent / Gallery / Downloads
  val getMultipleContentsLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetMultipleContents()
  ) { uris: List<Uri> ->
    uris.take(4).forEachIndexed { i, uri ->
      if (i in 0 until photoSlots.size) {
        photoSlots[i] = photoSlots[i].copy(isAttached = true, uri = uri)
      }
    }
  }

  fun launchGalleryForSlot(slot: Int) {
    activeSlotIndex = slot
    try {
      getContentLauncher.launch("image/*")
    } catch (_: Exception) {
      photoSlots[slot] = photoSlots[slot].copy(isAttached = true, uri = null)
    }
  }

  fun launchMultiGallery() {
    try {
      getMultipleContentsLauncher.launch("image/*")
    } catch (_: Exception) {
      for (k in 0 until photoSlots.size) {
        photoSlots[k] = photoSlots[k].copy(isAttached = true, uri = null)
      }
    }
  }

  val categories = listOf(
    "Mobile / Laptop",
    "Grocery & Daily Needs",
    "Local Services",
    "Electronics & Appliances",
    "Bikes & Two-Wheelers",
    "Cars & Four-Wheelers",
    "Personal Care & Beauty",
    "Fashion & Traditional Wear"
  )

  val attachedCount = photoSlots.count { it.isAttached }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("post_ad_dialog_surface")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Post Free Ad / Sell",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
            Text(
              text = "Upload 2 to 4 photos for your listing.",
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )
          }

          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .clickable { onDismiss() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Photos Section Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📸", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Photos ($attachedCount/4 Attached)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF334155)
            )
          }

          Surface(
            color = if (attachedCount >= 2) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = if (attachedCount >= 2) "✓ Ready to Post" else "Add 2+ Photos",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (attachedCount >= 2) Color(0xFF15803D) else Color(0xFFD97706),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4 Photo Slots Grid / Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          for (i in 0 until 4) {
            val slot = photoSlots[i]

            Box(
              modifier = Modifier
                .weight(1f)
                .height(105.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (slot.isAttached) Color(slot.sampleColor) else Color(0xFFF8FAFC))
                .border(
                  width = 1.5.dp,
                  color = if (slot.isAttached) Color(0xFF15803D) else Color(0xFFCBD5E1),
                  shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                  launchGalleryForSlot(i)
                }
                .testTag("photo_slot_$i"),
              contentAlignment = Alignment.Center
            ) {
              if (slot.isAttached) {
                if (slot.uri != null) {
                  // Device Gallery / Recent / Downloaded image
                  AsyncImage(
                    model = slot.uri,
                    contentDescription = "Selected photo ${i + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                  )
                } else {
                  // Attached photo card
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(4.dp)
                  ) {
                    Text(text = slot.sampleEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Photo ${i + 1}",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF15803D)
                    )
                  }
                }

                // Top right remove/delete button
                Box(
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDC2626))
                    .clickable {
                      photoSlots[i] = slot.copy(isAttached = false, uri = null)
                    },
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                  )
                }

                // Bottom badge
                Surface(
                  modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                  color = Color.Black.copy(alpha = 0.60f)
                ) {
                  Text(
                    text = slot.label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 2.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1
                  )
                }
              } else {
                // Empty state (+ Add)
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center,
                  modifier = Modifier.padding(4.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(30.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.AddAPhoto,
                      contentDescription = "Add Photo",
                      tint = Color(0xFF15803D),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "+ Add",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF15803D)
                  )
                  Text(
                    text = slot.label,
                    fontSize = 8.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Gallery & Sample Photos Action Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              val allAttached = photoSlots.all { it.isAttached }
              for (k in 0 until photoSlots.size) {
                photoSlots[k] = photoSlots[k].copy(isAttached = !allAttached, uri = null)
              }
            },
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .testTag("attach_all_photos_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF15803D)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (attachedCount >= 4) "Clear All" else "Attach All (4)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }

          OutlinedButton(
            onClick = { launchMultiGallery() },
            modifier = Modifier
              .weight(1f)
              .height(36.dp)
              .testTag("open_gallery_button"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0284C7)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBAE6FD))
          ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Phone Gallery",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title Field
        Text(
          text = "Title",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          placeholder = { Text("e.g. Fresh Organic Veggies / iPhone 13 128GB", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_title_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedBorderColor = Color(0xFF15803D)
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Dropdown
        Text(
          text = "Category",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box {
          OutlinedCard(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { categoryMenuExpanded = true }
              .testTag("ad_category_dropdown"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = selectedCategory,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF64748B)
              )
            }
          }

          DropdownMenu(
            expanded = categoryMenuExpanded,
            onDismissRequest = { categoryMenuExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(text = cat, fontSize = 14.sp) },
                onClick = {
                  selectedCategory = cat
                  isService = cat.contains("Services", ignoreCase = true)
                  categoryMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Price (₹) Field
        Text(
          text = "Price (₹)",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = priceText,
          onValueChange = { priceText = it },
          placeholder = { Text("e.g. 500", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_price_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedBorderColor = Color(0xFF15803D)
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Contact Number / WhatsApp Number Field
        Text(
          text = "Contact Number / WhatsApp Number",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = phoneOrWhatsapp,
          onValueChange = { phoneOrWhatsapp = it },
          placeholder = { Text("e.g. +91 98765 43210", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_phone_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedBorderColor = Color(0xFF15803D)
          ),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description Field
        Text(
          text = "Description",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          placeholder = { Text("Condition, location, warranty, etc.", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
          modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .testTag("ad_description_input"),
          shape = RoundedCornerShape(10.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedBorderColor = Color(0xFF15803D)
          ),
          maxLines = 4
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Post Listing Instantly Button
        Button(
          onClick = {
            val finalPrice = priceText.toDoubleOrNull() ?: 100.0
            val finalTitle = title.ifBlank { "Fresh Organic Produce" }
            onPostListing(
              finalTitle,
              selectedCategory,
              finalPrice,
              description.ifBlank { "Available directly in West Garo Hills local market." },
              isService,
              phoneOrWhatsapp
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("post_listing_submit_button"),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF15803D)
          )
        ) {
          Text(
            text = "Post Listing Instantly",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }
      }
    }
  }
}
