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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.local.ProductEntity

@Composable
fun ProductCard(
  product: ProductEntity,
  isFavorite: Boolean,
  onToggleFavorite: () -> Unit,
  onProductClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isService = product.badge.equals("SERVICE", ignoreCase = true) || product.category.equals("Services", ignoreCase = true)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onProductClick() }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color.White
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier.fillMaxWidth()
    ) {
      // Image Section with Tag and Favorite Button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .background(Color(0xFFE8F5E9))
      ) {
        // Product Hero Image
        Image(
          painter = painterResource(id = R.drawable.product_basket_hero_1786770186909),
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
        )

        // Overlay Badge (ITEM / SERVICE)
        Surface(
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.TopStart),
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFF1E293B)
        ) {
          Text(
            text = if (isService) "SERVICE" else "ITEM",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
          )
        }

        // Heart / Favorite button top right
        Box(
          modifier = Modifier
            .padding(8.dp)
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.9f))
            .clickable { onToggleFavorite() }
            .align(Alignment.TopEnd),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF64748B),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Details Section
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 10.dp, vertical = 10.dp)
      ) {
        // Price in Green
        val formattedPrice = if (product.price >= 1000) {
          String.format("₹%,.0f", product.price)
        } else {
          String.format("₹%.0f", product.price)
        }
        Text(
          text = formattedPrice,
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color(0xFF15803D)
        )

        Spacer(modifier = Modifier.height(3.dp))

        // Title
        Text(
          text = product.name,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF1E293B),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 17.sp,
          modifier = Modifier.height(34.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Location
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = product.vendorLocality,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Seller Name & Rating
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = product.vendorName,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = if (product.rating % 1.0 == 0.0) "${product.rating.toInt()}" else "${product.rating}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFD97706)
            )
          }
        }
      }
    }
  }
}
