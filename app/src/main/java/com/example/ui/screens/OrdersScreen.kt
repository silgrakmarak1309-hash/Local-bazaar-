package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.theme.FreshGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
  orders: List<OrderEntity>,
  onOrderClick: (OrderEntity) -> Unit,
  onAdvanceStatus: (String, String) -> Unit,
  onStartShoppingClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("orders_screen_list"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "My Local Orders",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Real-time tracking of neighborhood orders & deliveries",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (orders.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🛍️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No orders placed yet",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Explore fresh farm picks and neighborhood stalls to place your first local order.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = onStartShoppingClick,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
              )
            ) {
              Text("Browse Marketplace", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      items(orders) { order ->
        val isDelivered = order.orderStatus == "DELIVERED"
        val statusStep = when (order.orderStatus) {
          "CONFIRMED" -> 1
          "PACKED" -> 2
          "OUT_FOR_DELIVERY" -> 3
          "DELIVERED" -> 4
          else -> 1
        }

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick(order) }
            .testTag("order_card_${order.orderId}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Order ID & Date
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                  contentAlignment = Alignment.Center
                ) {
                  Text(text = "📦", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Order #${order.orderId}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = dateFormat.format(Date(order.placedAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // Status Chip
              val (statusBg, statusText) = when (order.orderStatus) {
                "DELIVERED" -> Pair(Color(0xFFDCFCE7), Color(0xFF166534))
                "OUT_FOR_DELIVERY" -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E))
                "PACKED" -> Pair(Color(0xFFE0E7FF), Color(0xFF3730A3))
                else -> Pair(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary)
              }

              Surface(
                color = statusBg,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = order.orderStatus.replace("_", " "),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  ),
                  color = statusText
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Tracking Progress Stepper
            Column(modifier = Modifier.fillMaxWidth()) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                TrackingStepCircle(step = 1, currentStep = statusStep, title = "Confirmed")
                TrackingStepLine(active = statusStep >= 2, modifier = Modifier.weight(1f))
                TrackingStepCircle(step = 2, currentStep = statusStep, title = "Packed")
                TrackingStepLine(active = statusStep >= 3, modifier = Modifier.weight(1f))
                TrackingStepCircle(step = 3, currentStep = statusStep, title = "On Way")
                TrackingStepLine(active = statusStep >= 4, modifier = Modifier.weight(1f))
                TrackingStepCircle(step = 4, currentStep = statusStep, title = "Delivered")
              }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Items Summary
            Text(
              text = "Items: ${order.itemsSummary}",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Vendor: ${order.vendorName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "₹${order.totalAmount.toInt()} (${order.paymentMethod})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulation Controls & Details Action
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (!isDelivered) {
                OutlinedButton(
                  onClick = {
                    val nextStatus = when (order.orderStatus) {
                      "CONFIRMED" -> "PACKED"
                      "PACKED" -> "OUT_FOR_DELIVERY"
                      "OUT_FOR_DELIVERY" -> "DELIVERED"
                      else -> "DELIVERED"
                    }
                    onAdvanceStatus(order.orderId, nextStatus)
                  },
                  modifier = Modifier
                    .weight(1f)
                    .height(38.dp),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Advance status",
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Advance Step (Test)", fontSize = 12.sp)
                }
              }

              Button(
                onClick = { onOrderClick(order) },
                modifier = Modifier
                  .weight(1f)
                  .height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.primaryContainer,
                  contentColor = MaterialTheme.colorScheme.primary
                )
              ) {
                Text("Live Tracking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TrackingStepCircle(
  step: Int,
  currentStep: Int,
  title: String,
  modifier: Modifier = Modifier
) {
  val isCompleted = currentStep >= step
  val isCurrent = currentStep == step

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
  ) {
    Box(
      modifier = Modifier
        .size(26.dp)
        .clip(CircleShape)
        .background(
          if (isCompleted) FreshGreen else MaterialTheme.colorScheme.surfaceVariant
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isCompleted) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = "Completed",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      } else {
        Text(
          text = step.toString(),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
        fontSize = 10.sp
      ),
      color = if (isCompleted) FreshGreen else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
private fun TrackingStepLine(
  active: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .padding(horizontal = 4.dp, vertical = 10.dp)
      .height(3.dp)
      .background(
        if (active) FreshGreen else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(2.dp)
      )
  )
}
