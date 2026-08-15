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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.local.CommunityRequestEntity
import com.example.data.local.MarketRateEntity
import com.example.ui.theme.FreshGreen

@Composable
fun CommunityAndRatesScreen(
  marketRates: List<MarketRateEntity>,
  communityRequests: List<CommunityRequestEntity>,
  onPostRequestClick: () -> Unit,
  onOfferFulfillment: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Daily Mandi Rates", "Community Board")

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
            modifier = Modifier.testTag("tab_$index")
          )
        }
      }

      if (selectedTab == 0) {
        // Daily Mandi Rates View
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("mandi_rates_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "🌾 Live Mandi Price Benchmark",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Updated daily at 6:30 AM from the local Agricultural Produce Market Committee (APMC) yard. Helps ensure fair pricing for both farmers and buyers.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )
              }
            }
          }

          items(marketRates) { rate ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(46.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = rate.emoji, fontSize = 24.sp)
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = rate.commodity,
                      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = rate.hindiName,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "Updated: ${rate.updatedTime}",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                  }
                }

                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = rate.mandiPrice,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  val (trendIcon, trendColor, trendBg) = when (rate.trend) {
                    "UP" -> Triple(Icons.Default.TrendingUp, Color(0xFFDC2626), Color(0xFFFEE2E2))
                    "DOWN" -> Triple(Icons.Default.TrendingDown, FreshGreen, Color(0xFFDCFCE7))
                    else -> Triple(Icons.Default.TrendingFlat, Color(0xFF4B5563), Color(0xFFF3F4F6))
                  }

                  Surface(
                    color = trendBg,
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = trendIcon,
                        contentDescription = "Trend",
                        tint = trendColor,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(2.dp))
                      Text(
                        text = rate.trendPercentage,
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = trendColor
                        )
                      )
                    }
                  }
                }
              }
            }
          }
        }
      } else {
        // Community Requests View
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .testTag("community_requests_list"),
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "📢 Neighborhood Demand Board",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Looking for special organic harvests, homemade pickles, or pooja essentials? Post your request here and nearby sellers & neighbors will respond!",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )
              }
            }
          }

          items(communityRequests) { req ->
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
                  verticalAlignment = Alignment.Top
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = req.title,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Category: ${req.category}",
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                      color = MaterialTheme.colorScheme.primary
                    )
                  }

                  Surface(
                    color = if (req.status == "OPEN") Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Text(
                      text = req.status,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (req.status == "OPEN") Color(0xFF166534) else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = req.description,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = "Location",
                      tint = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                      text = "${req.requesterName} • ${req.locality}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Text(
                    text = "${req.offersCount} Offers",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary
                  )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                  onClick = { onOfferFulfillment(req.id) },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("offer_button_${req.id}"),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                  )
                ) {
                  Icon(
                    imageVector = Icons.Default.Handshake,
                    contentDescription = "Offer",
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "I Have This / Send Offer",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Floating button to post request
    if (selectedTab == 1) {
      FloatingActionButton(
        onClick = onPostRequestClick,
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(bottom = 90.dp, end = 20.dp)
          .testTag("post_request_fab"),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "Post Request")
          Spacer(modifier = Modifier.width(4.dp))
          Text("Post Request", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
