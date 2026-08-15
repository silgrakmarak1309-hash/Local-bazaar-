package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MarketplaceRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MarketplaceRepository(application)

    val currentUser: StateFlow<UserProfile> = repository.currentUser
    val selectedLocation: StateFlow<SelectedLocation> = repository.selectedLocation
    val currentFilter: StateFlow<ListingFilter> = repository.currentFilter

    val listings: StateFlow<List<ListingItem>> = repository.getFilteredListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myListings: StateFlow<List<ListingItem>> = repository.getMyListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<ListingItem>> = repository.getFavoriteListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<Conversation>> = repository.getConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingReviewListings: StateFlow<List<ListingItem>> = repository.getPendingReviewListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMarketplaceListings: StateFlow<List<ListingItem>> = repository.getAllListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminUsers: StateFlow<List<AdminUserInfo>> = repository.getAdminUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedUserIds: StateFlow<Set<String>> = repository.blockedUserIds

    val notifications: StateFlow<List<AppNotification>> = repository.getNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val unreadMessagesCount: StateFlow<Int> = conversations.map { list ->
        list.sumOf { it.unreadCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Current Active Listing Detail Cache / Flow
    fun getListingById(id: String): Flow<ListingItem?> = repository.getListingById(id)

    fun getSellerListings(sellerId: String): Flow<List<ListingItem>> = repository.getSellerListings(sellerId)

    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> = repository.getMessagesForConversation(convId)

    fun getReviewsForUser(userId: String): Flow<List<Review>> = repository.getReviewsForUser(userId)

    fun setLocation(state: String, district: String, area: String) {
        repository.updateSelectedLocation(state, district, area)
        // Also update filter district/state
        repository.updateFilter(currentFilter.value.copy(state = state, district = district, area = null))
    }

    fun setFilter(filter: ListingFilter) {
        repository.updateFilter(filter)
    }

    fun setCategoryFilter(categoryName: String?, isService: Boolean? = null) {
        repository.updateFilter(
            currentFilter.value.copy(
                category = categoryName,
                isService = isService
            )
        )
    }

    fun setKeywordSearch(keyword: String) {
        repository.updateFilter(currentFilter.value.copy(keyword = keyword))
    }

    fun clearFilters() {
        repository.clearFilters()
    }

    fun toggleFavorite(listingId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(listingId)
        }
    }

    fun createListing(
        title: String,
        description: String,
        price: Double,
        isNegotiable: Boolean,
        category: String,
        subcategory: String,
        isService: Boolean,
        images: List<String>,
        condition: ItemCondition,
        state: String,
        district: String,
        area: String,
        isDraft: Boolean = false,
        onComplete: (String) -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.createListing(
                title = title,
                description = description,
                price = price,
                isNegotiable = isNegotiable,
                category = category,
                subcategory = subcategory,
                isService = isService,
                images = images,
                condition = condition,
                state = state,
                district = district,
                area = area,
                isDraft = isDraft
            )
            onComplete(id)
        }
    }

    fun updateListing(listing: ListingItem) {
        viewModelScope.launch {
            repository.updateListing(listing)
        }
    }

    fun deleteListing(id: String) {
        viewModelScope.launch {
            repository.deleteListing(id)
        }
    }

    fun markAsSold(id: String) {
        viewModelScope.launch {
            repository.markAsSold(id)
        }
    }

    fun toggleBoost(id: String, isBoosted: Boolean) {
        viewModelScope.launch {
            repository.toggleBoost(id, isBoosted)
        }
    }

    // Admin Moderation
    fun approveListing(id: String) {
        viewModelScope.launch {
            repository.approveListing(id)
        }
    }

    fun rejectListing(id: String, reason: String) {
        viewModelScope.launch {
            repository.rejectListing(id, reason)
        }
    }

    fun toggleBlockUser(userId: String, isBlocked: Boolean) {
        repository.toggleBlockUser(userId, isBlocked)
    }

    fun setListingStatus(id: String, status: ListingStatus, reason: String? = null) {
        viewModelScope.launch {
            repository.setListingStatus(id, status, reason)
        }
    }

    fun toggleListingSellStatus(id: String, currentStatus: ListingStatus) {
        viewModelScope.launch {
            val newStatus = if (currentStatus == ListingStatus.ACTIVE) ListingStatus.SOLD else ListingStatus.ACTIVE
            repository.setListingStatus(id, newStatus)
        }
    }


    // Chat
    fun getOrCreateConversation(listing: ListingItem, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val convId = repository.getOrCreateConversation(listing)
            onResult(convId)
        }
    }

    fun sendMessage(conversationId: String, text: String, targetUserId: String) {
        viewModelScope.launch {
            repository.sendMessage(conversationId, text, targetUserId)
        }
    }

    fun blockUser(conversationId: String, blocked: Boolean) {
        viewModelScope.launch {
            repository.blockUser(conversationId, blocked)
        }
    }

    fun markConversationRead(conversationId: String) {
        viewModelScope.launch {
            repository.markConversationRead(conversationId)
        }
    }

    // Safety Reports
    fun submitSafetyReport(targetType: String, targetId: String, reason: String, details: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.submitReport(targetType, targetId, reason, details)
            onDone()
        }
    }

    // Profile & Authentication
    fun setUserProfile(profile: UserProfile) {
        repository.setUserProfile(profile)
    }

    fun signOut(context: android.content.Context) {
        viewModelScope.launch {
            com.example.util.FirebaseAuthManager.signOut(context)
            repository.signOut()
        }
    }

    fun updateProfile(name: String, bio: String, state: String, district: String, area: String, isBusiness: Boolean, businessName: String?, businessCat: String?) {
        repository.updateProfile(name, bio, state, district, area, isBusiness, businessName, businessCat)
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun addReview(listingId: String, listingTitle: String, targetUserId: String, rating: Int, comment: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addReview(listingId, listingTitle, targetUserId, rating, comment)
            onDone()
        }
    }
}
