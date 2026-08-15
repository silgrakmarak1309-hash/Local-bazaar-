package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.UserRecord

@Composable
fun AdminControlDialog(
  userRecords: List<UserRecord>,
  onToggleBlockUser: (String) -> Unit,
  onDismiss: () -> Unit
) {
  val totalCount = userRecords.size
  val activeCount = userRecords.count { !it.isBlocked }
  val blockedCount = userRecords.count { it.isBlocked }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth()
        .height(600.dp)
        .testTag("admin_control_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFE4E6)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFFE11D48),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Admin User Moderation",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
              )
              Text(
                text = "Super Admin Control • User ID Records",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
              )
            }
          }

          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(Color(0xFFF1F5F9))
              .clickable { onDismiss() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color(0xFF64748B),
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats summary row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            modifier = Modifier.weight(1f),
            color = Color(0xFFEFF6FF),
            shape = RoundedCornerShape(10.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(text = "Total Users", fontSize = 11.sp, color = Color(0xFF1E40AF))
              Text(text = "$totalCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
            }
          }
          Surface(
            modifier = Modifier.weight(1f),
            color = Color(0xFFF0FDF4),
            shape = RoundedCornerShape(10.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(text = "Active Users", fontSize = 11.sp, color = Color(0xFF166534))
              Text(text = "$activeCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
            }
          }
          Surface(
            modifier = Modifier.weight(1f),
            color = Color(0xFFFEF2F2),
            shape = RoundedCornerShape(10.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(text = "Blocked Users", fontSize = 11.sp, color = Color(0xFF991B1B))
              Text(text = "$blockedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "User Records & Moderation",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334155)
          )
          Text(
            text = "$totalCount accounts",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // User records list
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .testTag("admin_user_records_list"),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(userRecords, key = { it.id }) { user ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .testTag("user_record_card_${user.id}"),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (user.isBlocked) Color(0xFFFFF1F2) else Color(0xFFF8FAFC)
              ),
              border = if (user.isBlocked) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECDD3)) else null
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp)
              ) {
                // Top line: User ID Badge, Role Badge, Status Badge
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      color = Color(0xFF1E293B),
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = user.id,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                      color = when (user.role) {
                        "SUPER ADMIN" -> Color(0xFFFFE4E6)
                        "SELLER" -> Color(0xFFF3E8FF)
                        else -> Color(0xFFE0F2FE)
                      },
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = user.role,
                        color = when (user.role) {
                          "SUPER ADMIN" -> Color(0xFFE11D48)
                          "SELLER" -> Color(0xFF7E22CE)
                          else -> Color(0xFF0369A1)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  // Status Badge
                  Surface(
                    color = if (user.isBlocked) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = if (user.isBlocked) "🚫 BLOCKED" else "✓ ACTIVE",
                      color = if (user.isBlocked) Color(0xFFDC2626) else Color(0xFF15803D),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.ExtraBold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Middle line: Avatar, Name, Email, Phone
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(if (user.isBlocked) Color(0xFFFCA5A5) else Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(text = user.avatarEmoji, fontSize = 18.sp)
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = user.name,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (user.isBlocked) Color(0xFF991B1B) else Color(0xFF1E293B),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                    Text(
                      text = "${user.email} • ${user.phone}",
                      fontSize = 11.sp,
                      color = Color(0xFF64748B),
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom line: Action button to Block / Unblock this user ID
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  if (user.role == "SUPER ADMIN") {
                    Text(
                      text = "🛡️ Protected Account",
                      fontSize = 11.sp,
                      color = Color(0xFF64748B),
                      fontWeight = FontWeight.Medium
                    )
                  } else {
                    if (user.isBlocked) {
                      Button(
                        onClick = { onToggleBlockUser(user.id) },
                        modifier = Modifier
                          .height(34.dp)
                          .testTag("unblock_user_button_${user.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                      ) {
                        Icon(
                          imageVector = Icons.Default.LockOpen,
                          contentDescription = null,
                          modifier = Modifier.size(14.dp),
                          tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "Unblock User",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.White
                        )
                      }
                    } else {
                      OutlinedButton(
                        onClick = { onToggleBlockUser(user.id) },
                        modifier = Modifier
                          .height(34.dp)
                          .testTag("block_user_button_${user.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                      ) {
                        Icon(
                          imageVector = Icons.Default.Block,
                          contentDescription = null,
                          modifier = Modifier.size(14.dp),
                          tint = Color(0xFFDC2626)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "Block User",
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFFDC2626)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
        ) {
          Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
