package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.OrderEntity
import com.example.ui.theme.FreshGreen

@Composable
fun OrderTrackingDialog(
  order: OrderEntity,
  onAdvanceStatus: (String, String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val statusStep = when (order.orderStatus) {
    "CONFIRMED" -> 1
    "PACKED" -> 2
    "OUT_FOR_DELIVERY" -> 3
    "DELIVERED" -> 4
    else -> 1
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)
        .testTag("order_tracking_dialog")
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
              text = "Live Order Tracking",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Order #${order.orderId}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary
            )
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ETA Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (order.orderStatus == "DELIVERED") Color(0xFFDCFCE7) else MaterialTheme.colorScheme.primaryContainer
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (order.orderStatus == "DELIVERED") Color(0xFFBBF7D0) else MaterialTheme.colorScheme.primary
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (order.orderStatus == "DELIVERED") Icons.Default.Check else Icons.Default.DirectionsBike,
                contentDescription = "Delivery status",
                tint = if (order.orderStatus == "DELIVERED") Color(0xFF166534) else Color.White
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = if (order.orderStatus == "DELIVERED") "Delivered Successfully!" else "Estimated Delivery: ${order.deliveryTimeEstimate}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = if (order.orderStatus == "DELIVERED") Color(0xFF166534) else MaterialTheme.colorScheme.onPrimaryContainer
              )
              Text(
                text = if (order.orderStatus == "DELIVERED") "Thank you for supporting local business" else "Neighborhood delivery in progress",
                style = MaterialTheme.typography.bodySmall,
                color = if (order.orderStatus == "DELIVERED") Color(0xFF166534) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tracking Timeline Steps
        TimelineStepItem(
          title = "Order Received & Confirmed",
          description = "Shop accepted order at ${order.vendorName}",
          completed = statusStep >= 1,
          isCurrent = statusStep == 1
        )
        TimelineStepItem(
          title = "Items Picked & Packed",
          description = "Fresh items packed hygienically with care",
          completed = statusStep >= 2,
          isCurrent = statusStep == 2
        )
        TimelineStepItem(
          title = "Out for Neighborhood Delivery",
          description = "Local partner is heading towards your location",
          completed = statusStep >= 3,
          isCurrent = statusStep == 3
        )
        TimelineStepItem(
          title = "Delivered to Doorstep",
          description = order.deliveryAddress,
          completed = statusStep >= 4,
          isCurrent = statusStep == 4,
          isLast = true
        )

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(14.dp))

        // Delivery Contact Actions
        Text(
          text = "Need assistance?",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = {
              val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+919876500000")
              }
              context.startActivity(dialIntent)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Call Shop", fontSize = 13.sp)
          }

          if (order.orderStatus != "DELIVERED") {
            Button(
              onClick = {
                val nextStatus = when (order.orderStatus) {
                  "CONFIRMED" -> "PACKED"
                  "PACKED" -> "OUT_FOR_DELIVERY"
                  "OUT_FOR_DELIVERY" -> "DELIVERED"
                  else -> "DELIVERED"
                }
                onAdvanceStatus(order.orderId, nextStatus)
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
              )
            ) {
              Icon(Icons.Default.Speed, contentDescription = "Next", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Advance (Test)", fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun TimelineStepItem(
  title: String,
  description: String,
  completed: Boolean,
  isCurrent: Boolean,
  isLast: Boolean = false
) {
  Row(modifier = Modifier.fillMaxWidth()) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .background(
            if (completed) FreshGreen else MaterialTheme.colorScheme.surfaceVariant
          ),
        contentAlignment = Alignment.Center
      ) {
        if (completed) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Completed",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        }
      }

      if (!isLast) {
        Box(
          modifier = Modifier
            .width(2.dp)
            .height(28.dp)
            .background(
              if (completed) FreshGreen else MaterialTheme.colorScheme.surfaceVariant
            )
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = if (isCurrent || completed) FontWeight.Bold else FontWeight.Normal
        ),
        color = if (completed) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
