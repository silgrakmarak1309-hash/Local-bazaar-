package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.ui.theme.FreshGreen

@Composable
fun SellerDashboardScreen(
  products: List<ProductEntity>,
  orders: List<OrderEntity>,
  onAddProductClick: () -> Unit,
  onToggleStock: (Int, Boolean) -> Unit,
  onDeleteProduct: (Int) -> Unit,
  onUpdateOrderStatus: (String, String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("My Inventory (${products.size})", "Incoming Orders (${orders.size})")

  Box(modifier = modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            modifier = Modifier.testTag("seller_tab_$index")
          )
        }
      }

      if (selectedTab == 0) {
        // Inventory Management
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("seller_inventory_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Seller summary metrics
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = "Farmer hub",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Local Seller & Farmer Hub",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "Add and manage products in real-time. Buyers in your neighborhood will immediately see updated stock and prices.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text("Total Items", style = MaterialTheme.typography.labelSmall)
                      Text(
                        "${products.size}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text("In Stock", style = MaterialTheme.typography.labelSmall)
                      Text(
                        "${products.count { it.inStock }}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = FreshGreen
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Text("Orders Placed", style = MaterialTheme.typography.labelSmall)
                      Text(
                        "${orders.size}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary
                      )
                    }
                  }
                }
              }
            }
          }

          items(products) { item ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = item.iconEmoji, fontSize = 24.sp)
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = item.name,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface,
                      maxLines = 1
                    )
                    Text(
                      text = "₹${item.price.toInt()} / ${item.unit} • ${item.category}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "Vendor: ${item.vendorName}",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = if (item.inStock) "In Stock" else "Sold Out",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (item.inStock) FreshGreen else MaterialTheme.colorScheme.error
                      )
                    )
                    Switch(
                      checked = item.inStock,
                      onCheckedChange = { onToggleStock(item.id, it) },
                      colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = FreshGreen
                      ),
                      modifier = Modifier.size(36.dp)
                    )
                  }

                  if (item.isCustomAdded) {
                    IconButton(
                      onClick = { onDeleteProduct(item.id) },
                      modifier = Modifier.size(36.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = MaterialTheme.colorScheme.error
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // Incoming Orders Processing
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("seller_orders_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(text = "📦", fontSize = 42.sp)
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "No customer orders yet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "When neighborhood customers buy from your stall, orders will appear here for processing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                  )
                }
              }
            }
          } else {
            items(orders) { order ->
              Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Order #${order.orderId}",
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                      color = MaterialTheme.colorScheme.primaryContainer,
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Text(
                        text = order.orderStatus.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  Text(
                    text = "Items: ${order.itemsSummary}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Text(
                    text = "Deliver To: ${order.deliveryAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Text(
                    text = "Instructions: ${order.deliveryInstructions}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                  )

                  Spacer(modifier = Modifier.height(10.dp))
                  HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                  Spacer(modifier = Modifier.height(10.dp))

                  // Vendor Order Status Advancement
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    when (order.orderStatus) {
                      "CONFIRMED" -> {
                        Button(
                          onClick = { onUpdateOrderStatus(order.orderId, "PACKED") },
                          modifier = Modifier.fillMaxWidth(),
                          shape = RoundedCornerShape(10.dp),
                          colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                          )
                        ) {
                          Icon(Icons.Default.Check, contentDescription = "Pack")
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("Mark Packed & Ready")
                        }
                      }
                      "PACKED" -> {
                        Button(
                          onClick = { onUpdateOrderStatus(order.orderId, "OUT_FOR_DELIVERY") },
                          modifier = Modifier.fillMaxWidth(),
                          shape = RoundedCornerShape(10.dp),
                          colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                          )
                        ) {
                          Icon(Icons.Default.LocalShipping, contentDescription = "Dispatch")
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("Dispatch For Delivery")
                        }
                      }
                      "OUT_FOR_DELIVERY" -> {
                        Button(
                          onClick = { onUpdateOrderStatus(order.orderId, "DELIVERED") },
                          modifier = Modifier.fillMaxWidth(),
                          shape = RoundedCornerShape(10.dp),
                          colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
                        ) {
                          Icon(Icons.Default.Check, contentDescription = "Delivered")
                          Spacer(modifier = Modifier.width(6.dp))
                          Text("Mark Successfully Delivered")
                        }
                      }
                      "DELIVERED" -> {
                        Surface(
                          color = Color(0xFFDCFCE7),
                          shape = RoundedCornerShape(10.dp),
                          modifier = Modifier.fillMaxWidth()
                        ) {
                          Text(
                            text = "✓ Order Completed & Paid (₹${order.totalAmount.toInt()})",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                              fontWeight = FontWeight.Bold,
                              color = Color(0xFF166534)
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // FAB to Add Product
    if (selectedTab == 0) {
      FloatingActionButton(
        onClick = onAddProductClick,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(bottom = 90.dp, end = 20.dp)
          .testTag("add_product_fab"),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Product")
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add Product", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
