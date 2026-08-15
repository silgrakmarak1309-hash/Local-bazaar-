package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Explore : Screen("explore", "Explore", Icons.Filled.Search, Icons.Outlined.Search)
    object Sell : Screen("sell", "Sell", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline)
    object Messages : Screen("messages", "Messages", Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person, Icons.Outlined.PersonOutline)
}

object NavDestinations {
    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"
    const val LISTING_DETAIL = "listing_detail/{listingId}"
    const val CHAT_CONVERSATION = "chat_conversation/{conversationId}"
    const val SAFETY_CENTER = "safety_center"
    const val ADMIN_MODERATION = "admin_moderation"
    const val NOTIFICATIONS = "notifications"

    fun listingDetail(listingId: String) = "listing_detail/$listingId"
    fun chatConversation(conversationId: String) = "chat_conversation/$conversationId"
}
