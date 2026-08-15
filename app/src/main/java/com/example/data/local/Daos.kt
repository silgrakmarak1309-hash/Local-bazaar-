package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings ORDER BY postedTimestamp DESC")
    fun getAllListings(): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    fun getListingById(id: String): Flow<ListingEntity?>

    @Query("SELECT * FROM listings WHERE id = :id LIMIT 1")
    suspend fun getListingDirect(id: String): ListingEntity?

    @Query("SELECT * FROM listings WHERE sellerId = :sellerId ORDER BY postedTimestamp DESC")
    fun getListingsBySeller(sellerId: String): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE status = 'PENDING_REVIEW' ORDER BY postedTimestamp DESC")
    fun getPendingReviewListings(): Flow<List<ListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ListingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListings(listings: List<ListingEntity>)

    @Update
    suspend fun updateListing(listing: ListingEntity)

    @Query("UPDATE listings SET status = :newStatus, rejectionReason = :reason WHERE id = :id")
    suspend fun updateListingStatus(id: String, newStatus: String, reason: String? = null)

    @Query("UPDATE listings SET isBoosted = :isBoosted WHERE id = :id")
    suspend fun updateBoostStatus(id: String, isBoosted: Boolean)

    @Query("DELETE FROM listings WHERE id = :id")
    suspend fun deleteListing(id: String)
}

@Dao
interface FavoriteDao {
    @Query("SELECT listingId FROM favorites")
    fun getFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE listingId = :listingId)")
    fun isFavorite(listingId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE listingId = :listingId")
    suspend fun removeFavorite(listingId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    fun getConversationById(id: String): Flow<ConversationEntity?>

    @Query("SELECT * FROM conversations WHERE listingId = :listingId AND buyerId = :buyerId LIMIT 1")
    suspend fun findConversationByListingAndBuyer(listingId: String, buyerId: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Query("UPDATE conversations SET isBlocked = :blocked WHERE id = :id")
    suspend fun setBlocked(id: String, blocked: Boolean)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :convId")
    suspend fun deleteMessagesForConversation(convId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE targetUserId = :userId ORDER BY timestamp DESC")
    fun getReviewsForUser(userId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("UPDATE reviews SET isReported = 1 WHERE id = :reviewId")
    suspend fun reportReview(reviewId: String)
}

@Dao
interface SafetyReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SafetyReportEntity)

    @Query("SELECT * FROM safety_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<SafetyReportEntity>>
}
