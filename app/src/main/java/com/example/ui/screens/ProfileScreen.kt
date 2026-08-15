package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.UserAccount

@Composable
fun ProfileScreen(
  currentUser: UserAccount,
  selectedState: String,
  selectedDistrict: String,
  onOpenAdminPanel: () -> Unit,
  onOpenLocationDialog: () -> Unit,
  onOpenPostAdDialog: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
      .verticalScroll(rememberScrollState())
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
          text = "Profile",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F172A)
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // User Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("user_profile_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Green Circle Avatar
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(Color(0xFF15803D)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(32.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = currentUser.name,
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = currentUser.email,
              fontSize = 12.sp,
              color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Role Badge
            Surface(
              color = if (currentUser.isAdmin) Color(0xFFFFE4E6) else Color(0xFFDCFCE7),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = currentUser.role,
                color = if (currentUser.isAdmin) Color(0xFFE11D48) else Color(0xFF15803D),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      // 1. Admin Control & Moderation Panel
      if (currentUser.isAdmin) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onOpenAdminPanel() }
            .testTag("admin_panel_entry_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFFFE4E6)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFFE11D48),
                modifier = Modifier.size(22.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Admin Control & Moderation Panel",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF991B1B)
              )
              Text(
                text = "Block / Unblock Users & Manage All Products",
                fontSize = 11.sp,
                color = Color(0xFFB91C1C)
              )
            }

            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = Color(0xFFE11D48)
            )
          }
        }
      }

      // 2. Change My Location
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { onOpenLocationDialog() }
          .testTag("change_location_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFFEF2F2)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = Color(0xFFEF4444),
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Change My Location",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "Current: $selectedDistrict, $selectedState",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }

          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8)
          )
        }
      }

      // 3. Sell Products & Services
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { onOpenPostAdDialog() }
          .testTag("sell_products_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = null,
              tint = Color(0xFF15803D),
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Sell Products & Services",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "Post a free classified ad in your town",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }

          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8)
          )
        }
      }

      // 4. Log Out
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .clickable { onLogout() }
          .testTag("logout_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ExitToApp,
              contentDescription = null,
              tint = Color(0xFF64748B),
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Log Out",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = "Sign out of your account",
              fontSize = 11.sp,
              color = Color(0xFF64748B)
            )
          }

          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8)
          )
        }
      }
    }
  }
}
