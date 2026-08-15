package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AddProductDialog(
  onAddProduct: (
    name: String,
    category: String,
    price: Double,
    originalPrice: Double,
    unit: String,
    vendorName: String,
    vendorLocality: String,
    vendorPhone: String,
    description: String,
    isFarmerDirect: Boolean,
    badge: String,
    emoji: String
  ) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Fresh Veggies & Fruits") }
  var priceText by remember { mutableStateOf("") }
  var originalPriceText by remember { mutableStateOf("") }
  var unit by remember { mutableStateOf("1 kg") }
  var vendorName by remember { mutableStateOf("My Neighborhood Stall") }
  var vendorLocality by remember { mutableStateOf("Bazar Ward No. 3") }
  var vendorPhone by remember { mutableStateOf("+91 98765 43210") }
  var description by remember { mutableStateOf("") }
  var isFarmerDirect by remember { mutableStateOf(true) }
  var badge by remember { mutableStateOf("Fresh Harvest") }
  var selectedEmoji by remember { mutableStateOf("🥦") }

  var categoryMenuExpanded by remember { mutableStateOf(false) }

  val categories = listOf(
    "Fresh Veggies & Fruits",
    "Daily Dairy & Bakery",
    "Groceries & Spices",
    "Pharmacy & Essentials",
    "Handicrafts & Artisans",
    "Sweets & Snacks"
  )

  val emojis = listOf("🥦", "🍅", "🥛", "🌾", "🍯", "🍞", "🍎", "🥔", "🏺", "🌿", "🍬", "🥭", "🛍️")

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("add_product_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Add Local Stall Product",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Emoji Picker Row
        Text("Select Item Icon", style = MaterialTheme.typography.labelMedium)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          emojis.take(7).forEach { emo ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = if (selectedEmoji == emo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .size(36.dp)
                .clickable { selectedEmoji = emo }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(emo, fontSize = 20.sp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Product Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Product Name *") },
          placeholder = { Text("e.g. Fresh Red Carrots") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Dropdown
        Box(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = category,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = {
              Text("▼ ", modifier = Modifier.clickable { categoryMenuExpanded = true })
            },
            modifier = Modifier
              .fillMaxWidth()
              .clickable { categoryMenuExpanded = true },
            shape = RoundedCornerShape(12.dp)
          )
          DropdownMenu(
            expanded = categoryMenuExpanded,
            onDismissRequest = { categoryMenuExpanded = false }
          ) {
            categories.forEach { cat ->
              DropdownMenuItem(
                text = { Text(cat) },
                onClick = {
                  category = cat
                  categoryMenuExpanded = false
                }
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price & Unit Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("Price (₹) *") },
            placeholder = { Text("40") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Unit / Quantity *") },
            placeholder = { Text("1 kg / 500g") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stall / Vendor Name
        OutlinedTextField(
          value = vendorName,
          onValueChange = { vendorName = it },
          label = { Text("Seller / Stall Name") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Freshness Note / Details") },
          placeholder = { Text("Harvested fresh this morning from field") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Direct Farmer Checkbox
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { isFarmerDirect = !isFarmerDirect }
        ) {
          Checkbox(
            checked = isFarmerDirect,
            onCheckedChange = { isFarmerDirect = it },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Direct Farmer / Harvest Item",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
          onClick = {
            val price = priceText.toDoubleOrNull() ?: 0.0
            val original = originalPriceText.toDoubleOrNull() ?: (price * 1.15)
            if (name.isNotBlank() && price > 0) {
              onAddProduct(
                name,
                category,
                price,
                original,
                unit,
                vendorName,
                vendorLocality,
                vendorPhone,
                description.ifBlank { "Locally sourced and freshly packed." },
                isFarmerDirect,
                badge.ifBlank { if (isFarmerDirect) "Fresh Harvest" else "Locally Sourced" },
                selectedEmoji
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("submit_product_button"),
          enabled = name.isNotBlank() && (priceText.toDoubleOrNull() ?: 0.0) > 0,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add")
          Spacer(modifier = Modifier.width(6.dp))
          Text("Publish To Local Bazaar", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
