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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatThread

@Composable
fun ChatsScreen(
  threads: List<ChatThread>,
  activeThread: ChatThread?,
  onOpenThread: (ChatThread) -> Unit,
  onCloseThread: () -> Unit,
  onSendMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var replyText by remember { mutableStateOf("") }

  if (activeThread != null) {
    // Live Active Conversation View
    Column(
      modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF1F5F9))
    ) {
      // Chat Top Header
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = onCloseThread) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = Color(0xFF1E293B)
            )
          }

          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = activeThread.avatarEmoji, fontSize = 20.sp)
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = activeThread.contactName,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B)
            )
            Text(
              text = activeThread.contactSubtitle,
              fontSize = 11.sp,
              color = Color(0xFF15803D)
            )
          }
        }
      }

      // Messages list
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(activeThread.messages, key = { it.id }) { msg ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
          ) {
            Surface(
              shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (msg.isMe) 14.dp else 2.dp,
                bottomEnd = if (msg.isMe) 2.dp else 14.dp
              ),
              color = if (msg.isMe) Color(0xFF15803D) else Color.White,
              shadowElevation = 1.dp,
              modifier = Modifier.fillMaxWidth(0.82f)
            ) {
              Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                  text = msg.text,
                  fontSize = 13.sp,
                  color = if (msg.isMe) Color.White else Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = msg.time,
                  fontSize = 10.sp,
                  color = if (msg.isMe) Color.White.copy(alpha = 0.7f) else Color(0xFF94A3B8),
                  modifier = Modifier.align(Alignment.End)
                )
              }
            }
          }
        }
      }

      // Chat input bar
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 3.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = replyText,
            onValueChange = { replyText = it },
            placeholder = { Text("Type a message to seller...", fontSize = 13.sp, color = Color(0xFF94A3B8)) },
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_field"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedBorderColor = Color(0xFFE2E8F0),
              focusedBorderColor = Color(0xFF15803D)
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.width(8.dp))

          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(Color(0xFF15803D))
              .clickable {
                if (replyText.isNotBlank()) {
                  onSendMessage(replyText)
                  replyText = ""
                }
              }
              .testTag("send_chat_message_button"),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = "Send",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  } else {
    // Threads List View
    Column(
      modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF8FAFC))
    ) {
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
            text = "Messages & Inquiries",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "Chat directly with local sellers and buyers",
            fontSize = 12.sp,
            color = Color(0xFF64748B)
          )
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 14.dp, vertical = 10.dp)
          .testTag("chats_threads_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(threads, key = { it.id }) { thread ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .clickable { onOpenThread(thread) }
              .testTag("chat_thread_${thread.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = thread.avatarEmoji, fontSize = 22.sp)
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = thread.contactName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                  )
                  Text(
                    text = thread.time,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                  )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = thread.lastMessage,
                  fontSize = 12.sp,
                  color = Color(0xFF64748B),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    }
  }
}
