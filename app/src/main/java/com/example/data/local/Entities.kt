package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ItemCondition
import com.example.model.ListingItem
import com.example.model.ListingStatus
import com.example.model.SellerVerification

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val isNegotiable: Boolean,
    val category: String,
    val subcategory: String,
    val isService: Boolean,
    val imagesCsv: String, // Comma-separated image references
    val condition: String,
    val state: String,
    val district: String,
    val area: String,
    val sellerId: String,
    val sellerName: String,
    val sellerAvatar: String,
    val sellerRating: Double,
    val sellerReviewCount: Int,
    val sellerBadge: String,
    val postedTimestamp: Long,
    val isFeatured: Boolean,
    val isBoosted: Boolean,
    val status: String,
    val rejectionReason: String?,
    val viewsCount: Int,
    val favoritesCount: Int,
    val businessName: String?
) {
    fun toListingItem(isFav: Boolean = false): ListingItem {
        val imageList = if (imagesCsv.isBlank()) emptyList() else imagesCsv.split("|")
        val parsedCondition = try {
            ItemCondition.valueOf(condition)
        } catch (e: Exception) {
            ItemCondition.GOOD
        }
        val parsedBadge = try {
            SellerVerification.valueOf(sellerBadge)
        } catch (e: Exception) {
            SellerVerification.VERIFIED_SELLER
        }
        val parsedStatus = try {
            ListingStatus.valueOf(status)
        } catch (e: Exception) {
            ListingStatus.ACTIVE
        }

        return ListingItem(
            id = id,
            title = title,
            description = description,
            price = price,
            isNegotiable = isNegotiable,
            category = category,
            subcategory = subcategory,
            isService = isService,
            images = imageList,
            condition = parsedCondition,
            state = state,
            district = district,
            area = area,
            sellerId = sellerId,
            sellerName = sellerName,
            sellerAvatar = sellerAvatar,
            sellerRating = sellerRating,
            sellerReviewCount = sellerReviewCount,
            sellerBadge = parsedBadge,
            postedTimestamp = postedTimestamp,
            isFeatured = isFeatured,
            isBoosted = isBoosted,
            status = parsedStatus,
            rejectionReason = rejectionReason,
            viewsCount = viewsCount,
            favoritesCount = favoritesCount,
            isFavorite = isFav,
            businessName = businessName
        )
    }

    companion object {
        fun fromListingItem(item: ListingItem): ListingEntity {
            return ListingEntity(
                id = item.id,
                title = item.title,
                description = item.description,
                price = item.price,
                isNegotiable = item.isNegotiable,
                category = item.category,
                subcategory = item.subcategory,
                isService = item.isService,
                imagesCsv = item.images.joinToString("|"),
                condition = item.condition.name,
                state = item.state,
                district = item.district,
                area = item.area,
                sellerId = item.sellerId,
                sellerName = item.sellerName,
                sellerAvatar = item.sellerAvatar,
                sellerRating = item.sellerRating,
                sellerReviewCount = item.sellerReviewCount,
                sellerBadge = item.sellerBadge.name,
                postedTimestamp = item.postedTimestamp,
                isFeatured = item.isFeatured,
                isBoosted = item.isBoosted,
                status = item.status.name,
                rejectionReason = item.rejectionReason,
                viewsCount = item.viewsCount,
                favoritesCount = item.favoritesCount,
                businessName = item.businessName
            )
        }
    }
}

@Entity(tableName = "favorites", primaryKeys = ["listingId"])
data class FavoriteEntity(
    val listingId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val listingId: String,
    val listingTitle: String,
    val listingPrice: Double,
    val listingImage: String,
    val listingArea: String,
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val sellerName: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int,
    val isBlocked: Boolean
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: Long,
    val isRead: Boolean,
    val isSystemNotice: Boolean
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String,
    val timestamp: Long,
    val isRead: Boolean,
    val referenceId: String?
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val listingId: String,
    val listingTitle: String,
    val reviewerId: String,
    val reviewerName: String,
    val reviewerAvatar: String,
    val targetUserId: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long,
    val isReported: Boolean
)

@Entity(tableName = "safety_reports")
data class SafetyReportEntity(
    @PrimaryKey val id: String,
    val targetType: String,
    val targetId: String,
    val reason: String,
    val details: String,
    val reporterId: String,
    val timestamp: Long,
    val status: String
)
