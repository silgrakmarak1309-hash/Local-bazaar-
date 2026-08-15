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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun PostCommunityRequestDialog(
  onPostRequest: (
    title: String,
    description: String,
    category: String,
    requesterName: String,
    locality: String
  ) -> Unit,
  onDismiss: () -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Fresh Veggies & Fruits") }
  var requesterName by remember { mutableStateOf("") }
  var locality by remember { mutableStateOf("Bazar Ward No. 3") }

  var categoryMenuExpanded by remember { mutableStateOf(false) }

  val categories = listOf(
    "Fresh Veggies & Fruits",
    "Daily Dairy & Bakery",
    "Groceries & Spices",
    "Pharmacy & Essentials",
    "Handicrafts & Artisans",
    "Sweets & Snacks"
  )

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("post_community_request_dialog")
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
            text = "Post Neighborhood Request",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("What item are you looking for? *") },
          placeholder = { Text("e.g. Need 5kg Fresh Country Guavas") },
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

        // Description
        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Details / Quantity / Requirements *") },
          placeholder = { Text("Looking for organically grown produce for family event...") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 3,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Requester Name
        OutlinedTextField(
          value = requesterName,
          onValueChange = { requesterName = it },
          label = { Text("Your Name / House") },
          placeholder = { Text("e.g. Anjali Sharma") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Locality
        OutlinedTextField(
          value = locality,
          onValueChange = { locality = it },
          label = { Text("Locality / Sector") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = {
            if (title.isNotBlank() && description.isNotBlank()) {
              onPostRequest(
                title,
                description,
                category,
                requesterName.ifBlank { "Neighbor" },
                locality.ifBlank { "Bazar Ward No. 3" }
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("submit_request_button"),
          enabled = title.isNotBlank() && description.isNotBlank(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.Send, contentDescription = "Post")
          Spacer(modifier = Modifier.width(6.dp))
          Text("Post To Community Board", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
