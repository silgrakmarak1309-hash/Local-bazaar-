package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CategoryGridItem(
  val title: String,
  val categoryKey: String,
  val emoji: String,
  val subtitle: String,
  val bgColor: Long
)

@Composable
fun CategoriesScreen(
  onSelectCategory: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val allCategories = listOf(
    CategoryGridItem("Mobile / Laptop", "Mobile / Laptop", "📱", "Smartphones, Laptops & MacBooks", 0xFFE0F2FE),
    CategoryGridItem("Grocery & Farm", "Grocery", "🥦", "Fresh vegetables, fruits & honey", 0xFFDCFCE7),
    CategoryGridItem("Local Services", "Services", "🔧", "Electricians, plumbers & mechanics", 0xFFFEF3C7),
    CategoryGridItem("Electronics & TV", "Electronics", "📺", "Smart TVs, audio & home appliances", 0xFFEDE9FE),
    CategoryGridItem("Bikes & Two-Wheelers", "Bike", "🏍️", "Motorcycles, scooties & superbikes", 0xFFFFEDD5),
    CategoryGridItem("Cars & Four-Wheelers", "Car", "🚗", "Hatchbacks, SUVs & sedans", 0xFFE0E7FF),
    CategoryGridItem("Personal Care & Beauty", "Personal Care", "🧴", "Ayurvedic oils, skincare & beauty", 0xFFCCFBF1),
    CategoryGridItem("Fashion & Traditional Wear", "Fashion & Wear", "👗", "Handloom, shawls & apparel", 0xFFFCE7F3)
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Header
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = Color.White,
      shadowElevation = 1.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Text(
          text = "Marketplace Categories",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F172A)
        )
        Text(
          text = "Explore neighborhood listings by category",
          fontSize = 12.sp,
          color = Color(0xFF64748B)
        )
      }
    }

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier
        .fillMaxSize()
        .testTag("categories_grid"),
      contentPadding = PaddingValues(14.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(allCategories) { item ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelectCategory(item.categoryKey) }
            .testTag("category_card_${item.categoryKey}"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(item.bgColor)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = item.emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = item.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = item.subtitle,
              fontSize = 11.sp,
              color = Color(0xFF64748B),
              lineHeight = 14.sp
            )
          }
        }
      }
    }
  }
}
