package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.local.ProductEntity

@Composable
fun ProductDetailDialog(
  product: ProductEntity,
  cartQuantity: Int,
  onAddToCart: () -> Unit,
  onUpdateQuantity: (Int) -> Unit,
  onChatWithSeller: (vendorName: String, locality: String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val isService = product.badge.equals("SERVICE", ignoreCase = true) || product.category.contains("Service", ignoreCase = true)

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)
        .testTag("product_detail_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            color = if (isService) Color(0xFFFEF3C7) else Color(0xFFDCFCE7),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(
              text = if (isService) "SERVICE • ${product.category}" else "ITEM • ${product.category}",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isService) Color(0xFF92400E) else Color(0xFF15803D)
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hero Image
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8F5E9))
        ) {
          Image(
            painter = painterResource(id = R.drawable.product_basket_hero_1786770186909),
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(140.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Product Name & Price
        Text(
          text = product.name,
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          val formattedPrice = if (product.price >= 1000) {
            String.format("₹%,.0f", product.price)
          } else {
            String.format("₹%.0f", product.price)
          }
          Text(
            text = formattedPrice,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF15803D)
          )

          if (product.originalPrice > product.price) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "₹${product.originalPrice.toInt()}",
              fontSize = 14.sp,
              style = androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough),
              color = Color(0xFF94A3B8)
            )
          }

          Spacer(modifier = Modifier.weight(1f))

          Text(
            text = product.unit,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Description
        Text(
          text = "About this listing",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF334155)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = product.description,
          fontSize = 12.sp,
          color = Color(0xFF64748B),
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = Color(0xFFE2E8F0))
        Spacer(modifier = Modifier.height(12.dp))

        // Seller Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = product.vendorName,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "${product.vendorLocality} (${product.distanceKm} km)",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }

          // Call button
          OutlinedButton(
            onClick = {
              val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${product.vendorPhone}")
              }
              context.startActivity(dialIntent)
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(36.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp), tint = Color(0xFF15803D))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Call", fontSize = 12.sp, color = Color(0xFF15803D))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row (Chat with seller & Direct WhatsApp)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              onDismiss()
              onChatWithSeller(product.vendorName, product.vendorLocality)
            },
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("in_app_chat_button"),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF0284C7))
            Spacer(modifier = Modifier.width(4.dp))
            Text("In-App Chat", fontSize = 12.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              val rawPhone = product.vendorPhone.filter { it.isDigit() }
              val phoneWithCode = when {
                rawPhone.startsWith("91") && rawPhone.length >= 12 -> rawPhone
                rawPhone.length == 10 -> "91$rawPhone"
                else -> rawPhone.ifEmpty { "919876511001" }
              }
              val messageText = "Hi ${product.vendorName}, I am interested in your listing '${product.name}' (₹${product.price.toInt()}) on Local Bazaar."
              val encodedMessage = Uri.encode(messageText)
              val whatsappUrl = "https://api.whatsapp.com/send?phone=$phoneWithCode&text=$encodedMessage"

              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl)).apply {
                setPackage("com.whatsapp")
              }
              try {
                context.startActivity(intent)
              } catch (e: Exception) {
                try {
                  val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phoneWithCode?text=$encodedMessage"))
                  context.startActivity(browserIntent)
                } catch (e2: Exception) {
                  val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${product.vendorPhone}"))
                  context.startActivity(dialIntent)
                }
              }
            },
            modifier = Modifier
              .weight(1.25f)
              .height(46.dp)
              .testTag("whatsapp_contact_button"),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
          ) {
            Text(text = "💬", fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }
        }
      }
    }
  }
}
