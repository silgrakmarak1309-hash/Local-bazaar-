package com.example.model

data class UserProfile(
    val id: String = "usr_current_me",
    val name: String = "Aarav Sharma",
    val phone: String = "+91 98765 43210",
    val email: String = "aarav.sharma@example.in",
    val avatarUrl: String = "",
    val state: String = "Maharashtra",
    val district: String = "Mumbai Suburban",
    val area: String = "Bandra West",
    val joinDate: String = "March 2024",
    val verificationBadge: SellerVerification = SellerVerification.VERIFIED_SELLER,
    val rating: Double = 4.9,
    val reviewCount: Int = 18,
    val isBusiness: Boolean = false,
    val businessName: String? = null,
    val businessCategory: String? = null,
    val businessHours: String? = null,
    val businessDescription: String? = null,
    val aboutBio: String = "Active buyer & seller in Bandra & Mumbai Suburban. Fast responder!"
) {
    val phoneNumber: String get() = phone
    val joinedDate: String get() = joinDate
    val bio: String get() = aboutBio
    val isBusinessAccount: Boolean get() = isBusiness
    val isAdmin: Boolean get() = email.equals("silgrakmarak1309@gmail.com", ignoreCase = true) ||
            email.contains("admin", ignoreCase = true) ||
            name.contains("Silgrak", ignoreCase = true)
}

data class Review(
    val id: String,
    val listingId: String,
    val listingTitle: String,
    val reviewerId: String,
    val reviewerName: String,
    val reviewerAvatar: String = "",
    val targetUserId: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isReported: Boolean = false
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true,
    val isSystemNotice: Boolean = false
)

data class Conversation(
    val id: String,
    val listingId: String,
    val listingTitle: String,
    val listingPrice: Double,
    val listingImage: String = "",
    val listingArea: String = "",
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val sellerName: String,
    val lastMessage: String,
    val lastTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isBlocked: Boolean = false
) {
    val otherUserName: String get() = if (buyerId == "usr_current_me") sellerName else buyerName
    val otherUserId: String get() = if (buyerId == "usr_current_me") sellerId else buyerId
    val lastMessageText: String get() = lastMessage
    val lastMessageTime: Long get() = lastTimestamp
}

data class SafetyReport(
    val id: String,
    val targetType: String, // "Listing", "User", "Review", "Message"
    val targetId: String,
    val reason: String,
    val details: String,
    val reporterId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending Investigation"
)

enum class NotificationType(val title: String) {
    MESSAGE("New Message"),
    APPROVAL("Listing Approved"),
    REJECTION("Listing Needs Update"),
    FAVORITE_UPDATE("Price Drop Alert"),
    NEARBY_ALERT("New in Your Area"),
    SECURITY("Security & Safety Alert")
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val referenceId: String? = null
) {
    val actionTargetType: String get() = when (type) {
        NotificationType.MESSAGE -> "Chat"
        NotificationType.APPROVAL, NotificationType.REJECTION, NotificationType.FAVORITE_UPDATE -> "Listing"
        else -> "General"
    }
    val actionTargetId: String? get() = referenceId
}

data class AdminUserInfo(
    val userId: String,
    val name: String,
    val phone: String,
    val email: String = "",
    val location: String,
    val listingsCount: Int = 0,
    val isBlocked: Boolean = false,
    val verificationBadge: SellerVerification = SellerVerification.VERIFIED_SELLER
)
