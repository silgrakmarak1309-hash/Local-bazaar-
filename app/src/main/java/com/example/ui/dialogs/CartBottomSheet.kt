package com.example.ui.dialogs

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CartSummary
import com.example.ui.theme.FreshGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartBottomSheet(
  cartSummary: CartSummary,
  onUpdateQuantity: (Int, Int) -> Unit,
  onRemoveItem: (Int) -> Unit,
  onClearCart: () -> Unit,
  onPlaceOrder: (String, String, String) -> Unit,
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var deliveryAddress by remember { mutableStateOf("House 14, Bazar Ward No. 3, Near Old Water Tank") }
  var deliveryInstructions by remember { mutableStateOf("Ring bell and leave at door") }
  var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }

  val paymentMethods = listOf(
    Pair("Cash on Delivery", "💵"),
    Pair("UPI / Local QR Pay", "📱"),
    Pair("Pay at Stall Pickup", "🏪")
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = Modifier.testTag("cart_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 30.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Title Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = "Cart",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "My Local Basket (${cartSummary.itemCount})",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        if (cartSummary.items.isNotEmpty()) {
          IconButton(onClick = onClearCart) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Clear cart",
              tint = MaterialTheme.colorScheme.error
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      if (cartSummary.items.isEmpty()) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🛒", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Your basket is empty",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Add fresh produce or local shop goods to check out.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        // Items List
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          cartSummary.items.forEach { itemWithProd ->
            val product = itemWithProd.product
            val cartItem = itemWithProd.cartItem

            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = product.iconEmoji, fontSize = 20.sp)
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column {
                    Text(
                      text = product.name,
                      style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1
                    )
                    Text(
                      text = "₹${product.price.toInt()} × ${cartItem.quantity} = ₹${(product.price * cartItem.quantity).toInt()}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                // Quantity Stepper
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 2.dp, vertical = 2.dp)
                ) {
                  IconButton(
                    onClick = { onUpdateQuantity(product.id, cartItem.quantity - 1) },
                    modifier = Modifier.size(26.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Remove,
                      contentDescription = "Decrease",
                      modifier = Modifier.size(12.dp)
                    )
                  }
                  Text(
                    text = "${cartItem.quantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 6.dp)
                  )
                  IconButton(
                    onClick = { onUpdateQuantity(product.id, cartItem.quantity + 1) },
                    modifier = Modifier.size(26.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Increase",
                      modifier = Modifier.size(12.dp)
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delivery Address
        Text(
          text = "Delivery Address (Neighborhood)",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = deliveryAddress,
          onValueChange = { deliveryAddress = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_address_input"),
          leadingIcon = {
            Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = MaterialTheme.colorScheme.secondary)
          },
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Delivery Instructions
        Text(
          text = "Special Delivery Note",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = deliveryInstructions,
          onValueChange = { deliveryInstructions = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("delivery_instructions_input"),
          placeholder = { Text("e.g. Leave at gate, call on reaching", fontSize = 13.sp) },
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method
        Text(
          text = "Payment Method",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))

        paymentMethods.forEach { (method, emoji) ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .selectable(
                selected = (selectedPaymentMethod == method),
                onClick = { selectedPaymentMethod = method }
              )
              .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            RadioButton(
              selected = (selectedPaymentMethod == method),
              onClick = { selectedPaymentMethod = method },
              colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "$emoji $method", style = MaterialTheme.typography.bodyMedium)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(14.dp))

        // Bill Summary
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Item Total (Subtotal)", style = MaterialTheme.typography.bodyMedium)
            Text("₹${cartSummary.subtotal.toInt()}", fontWeight = FontWeight.Bold)
          }

          if (cartSummary.savings > 0) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Local Bazaar Savings", style = MaterialTheme.typography.bodyMedium, color = FreshGreen)
              Text("- ₹${cartSummary.savings.toInt()}", fontWeight = FontWeight.Bold, color = FreshGreen)
            }
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium)
            Text(
              if (cartSummary.deliveryFee == 0.0) "FREE" else "₹${cartSummary.deliveryFee.toInt()}",
              fontWeight = FontWeight.Bold,
              color = if (cartSummary.deliveryFee == 0.0) FreshGreen else MaterialTheme.colorScheme.onSurface
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Local Community Care Fee", style = MaterialTheme.typography.bodyMedium)
            Text("₹${cartSummary.platformFee.toInt()}", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(4.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          Spacer(modifier = Modifier.height(4.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              "To Pay",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              "₹${cartSummary.total.toInt()}",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Place Order Button
        Button(
          onClick = {
            onPlaceOrder(deliveryAddress, deliveryInstructions, selectedPaymentMethod)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("place_order_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Icon(Icons.Default.DeliveryDining, contentDescription = "Order")
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Place Local Order • ₹${cartSummary.total.toInt()}",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }
      }
    }
  }
}
