package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.ProductEntity
import com.example.ui.components.BazaarTopBar
import com.example.ui.components.ProductCard

data class CategoryItem(val name: String, val emoji: String)

@Composable
fun HomeScreen(
  products: List<ProductEntity>,
  selectedState: String,
  selectedDistrict: String,
  onOpenLocationSelector: () -> Unit,
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  selectedCategory: String,
  onCategorySelected: (String) -> Unit,
  filterType: String,
  onFilterTypeSelected: (String) -> Unit,
  favorites: Set<Int>,
  onToggleFavorite: (Int) -> Unit,
  onProductClick: (ProductEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  val categories = listOf(
    CategoryItem("All", "⚡"),
    CategoryItem("Mobile / Laptop", "📱"),
    CategoryItem("Grocery", "🥦"),
    CategoryItem("Services", "🔧"),
    CategoryItem("Electronics", "📺"),
    CategoryItem("Bike", "🏍️"),
    CategoryItem("Car", "🚗"),
    CategoryItem("Personal Care", "🧴"),
    CategoryItem("Fashion & Wear", "👗")
  )

  // Filter products based on search, category, and filter chip
  val filteredProducts = products.filter { product ->
    val matchesSearch = searchQuery.isBlank() ||
      product.name.contains(searchQuery, ignoreCase = true) ||
      product.category.contains(searchQuery, ignoreCase = true) ||
      product.vendorName.contains(searchQuery, ignoreCase = true) ||
      product.vendorLocality.contains(searchQuery, ignoreCase = true)

    val matchesCategory = selectedCategory == "All" ||
      product.category.equals(selectedCategory, ignoreCase = true) ||
      (selectedCategory.contains("Mobile", ignoreCase = true) && (product.category.contains("Mobile", ignoreCase = true) || product.category.contains("Laptop", ignoreCase = true))) ||
      (selectedCategory.contains("Grocery", ignoreCase = true) && product.category.contains("Grocery", ignoreCase = true)) ||
      (selectedCategory.contains("Service", ignoreCase = true) && (product.category.contains("Service", ignoreCase = true) || product.badge.equals("SERVICE", ignoreCase = true))) ||
      (selectedCategory.contains("Electronics", ignoreCase = true) && product.category.contains("Electr", ignoreCase = true)) ||
      (selectedCategory.equals("Bike", ignoreCase = true) && (product.category.contains("Bike", ignoreCase = true) || product.category.contains("Two-Wheeler", ignoreCase = true))) ||
      (selectedCategory.equals("Car", ignoreCase = true) && (product.category.contains("Car", ignoreCase = true) || product.category.contains("Four-Wheeler", ignoreCase = true))) ||
      (selectedCategory.contains("Personal Care", ignoreCase = true) && product.category.contains("Personal Care", ignoreCase = true)) ||
      (selectedCategory.contains("Fashion", ignoreCase = true) && product.category.contains("Fashion", ignoreCase = true))

    val matchesFilterType = when (filterType) {
      "VERIFIED" -> product.rating >= 4.8 || product.isFarmerDirect
      "SERVICES" -> product.badge.equals("SERVICE", ignoreCase = true) || product.category.contains("Service", ignoreCase = true)
      "PRODUCTS" -> !product.badge.equals("SERVICE", ignoreCase = true)
      else -> true
    }

    matchesSearch && matchesCategory && matchesFilterType
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Top Bar
    BazaarTopBar(
      selectedState = selectedState,
      selectedDistrict = selectedDistrict,
      onLocationClick = onOpenLocationSelector,
      searchQuery = searchQuery,
      onSearchQueryChange = onSearchQueryChange
    )

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier
        .fillMaxSize()
        .testTag("home_products_grid"),
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. Hero Green Banner
      item(span = { GridItemSpan(2) }) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
          shape = RoundedCornerShape(16.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(Color(0xFF15803D), Color(0xFF16A34A))
                )
              )
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(
                modifier = Modifier.weight(1f)
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color.White.copy(alpha = 0.2f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.Star,
                      contentDescription = null,
                      tint = Color(0xFFFBBF24),
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                      text = "LOCAL SUPERSTORE",
                      color = Color.White,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = "Buy & Sell Locally",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = "Connect instantly with verified neighborhood sellers & fresh local grocery markets.",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.9f),
                  lineHeight = 15.sp
                )
              }

              Spacer(modifier = Modifier.width(10.dp))

              Box(
                modifier = Modifier
                  .size(76.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
              ) {
                Image(
                  painter = painterResource(id = R.drawable.product_basket_hero_1786770186909),
                  contentDescription = "Basket",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }
          }
        }
      }

      // 2. Categories Horizontal Scroll Row
      item(span = { GridItemSpan(2) }) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { onCategorySelected(cat.name) }
                .testTag("category_item_${cat.name}")
            ) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) Color(0xFF15803D) else Color.White),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = cat.emoji,
                  fontSize = 22.sp
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = cat.name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF15803D) else Color(0xFF475569)
              )
            }
          }
        }
      }

      // 3. Filter Chips Row
      item(span = { GridItemSpan(2) }) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Verified Sellers Chip
          FilterChip(
            selected = filterType == "VERIFIED",
            onClick = { onFilterTypeSelected("VERIFIED") },
            label = {
              Text(
                text = "Verified Sellers",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = if (filterType == "VERIFIED") Color(0xFF15803D) else Color(0xFF64748B),
                modifier = Modifier.size(14.dp)
              )
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFDCFCE7),
              selectedLabelColor = Color(0xFF15803D),
              containerColor = Color.White,
              labelColor = Color(0xFF475569)
            ),
            modifier = Modifier.testTag("filter_verified_sellers")
          )

          // Services Only Chip
          FilterChip(
            selected = filterType == "SERVICES",
            onClick = { onFilterTypeSelected("SERVICES") },
            label = {
              Text(
                text = "Services Only",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            },
            leadingIcon = {
              Text(text = "🤝", fontSize = 12.sp)
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFDCFCE7),
              selectedLabelColor = Color(0xFF15803D),
              containerColor = Color.White,
              labelColor = Color(0xFF475569)
            ),
            modifier = Modifier.testTag("filter_services_only")
          )

          // Products Chip
          FilterChip(
            selected = filterType == "PRODUCTS",
            onClick = { onFilterTypeSelected("PRODUCTS") },
            label = {
              Text(
                text = "Products",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            },
            leadingIcon = {
              Text(text = "🛍️", fontSize = 12.sp)
            },
            shape = RoundedCornerShape(20.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFDCFCE7),
              selectedLabelColor = Color(0xFF15803D),
              containerColor = Color.White,
              labelColor = Color(0xFF475569)
            ),
            modifier = Modifier.testTag("filter_products_only")
          )
        }
      }

      // 4. Section Title Header: "Popular Near You (X items found)"
      item(span = { GridItemSpan(2) }) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Popular Near You",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "(${filteredProducts.size} items found)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
          )
        }
      }

      // 5. Products 2-Column Grid Items
      items(filteredProducts, key = { it.id }) { product ->
        ProductCard(
          product = product,
          isFavorite = favorites.contains(product.id),
          onToggleFavorite = { onToggleFavorite(product.id) },
          onProductClick = { onProductClick(product) }
        )
      }
    }
  }
}
