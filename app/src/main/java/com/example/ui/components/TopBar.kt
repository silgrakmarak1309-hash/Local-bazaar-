package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun BazaarTopBar(
  selectedState: String,
  selectedDistrict: String,
  onLocationClick: () -> Unit,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color.White,
    shadowElevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      // Top row: App Brand and Location Chip
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo + App Name + Subtitle
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFE8F5E9))
          ) {
            Image(
              painter = painterResource(id = R.drawable.meri_local_bazaar_logo_1786770166058),
              contentDescription = "Meri Local Bazaar Logo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth().height(42.dp)
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Text(
              text = "Meri Local Bazaar",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF15803D)
            )
            Text(
              text = "Your Local Market, One App",
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = Color(0xFF64748B)
            )
          }
        }

        // Location Pill
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onLocationClick() }
            .testTag("location_selector_chip"),
          shape = RoundedCornerShape(20.dp),
          color = Color(0xFFF1F5F9)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Location",
              tint = Color(0xFFDC2626),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$selectedDistrict, ${selectedState.take(7)}...",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF334155),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Search Bar
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(44.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF1F5F9)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
              Text(
                text = "Search groceries, mobiles, services, items...",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
              )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              disabledContainerColor = Color.Transparent,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("search_text_input")
          )
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { onSearchQueryChange("") },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
              )
            }
          }
        }
      }
    }
  }
}
