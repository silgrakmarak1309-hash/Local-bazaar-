package com.example.ui.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotification
import com.example.model.NotificationType
import com.example.ui.components.EmptyStateView
import com.example.ui.screens.chat.formatChatTime
import com.example.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onOpenListing: (String) -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                            Text("Mark all read", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Outlined.NotificationsNone,
                title = "No notifications yet",
                message = "You will receive updates when buyers message you, listings are approved, or prices drop.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(notifications) { notif ->
                    NotificationItemRow(
                        notification = notif,
                        onClick = {
                            viewModel.markNotificationRead(notif.id)
                            val targetId = notif.actionTargetId
                            if (notif.actionTargetType == "Listing" && targetId != null) {
                                onOpenListing(targetId)
                            } else if (notif.actionTargetType == "Chat") {
                                onOpenChat()
                            }
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
fun NotificationItemRow(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val (icon, tint) = when (notification.type) {
        NotificationType.APPROVAL -> Icons.Default.CheckCircle to Color(0xFF16A34A)
        NotificationType.MESSAGE -> Icons.Default.Chat to MaterialTheme.colorScheme.primary
        NotificationType.FAVORITE_UPDATE -> Icons.Default.TrendingDown to Color(0xFFE11D48)
        NotificationType.SECURITY -> Icons.Default.Shield to Color(0xFFD97706)
        else -> Icons.Default.Notifications to MaterialTheme.colorScheme.primary
    }

    Surface(
        color = if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("notification_item_${notification.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!notification.isRead) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                    Text(
                        text = formatChatTime(notification.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
