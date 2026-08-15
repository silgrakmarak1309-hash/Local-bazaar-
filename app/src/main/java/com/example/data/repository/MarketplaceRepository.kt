package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MarketplaceRepository(private val context: Context) {
    private val database = LocalBazaarDatabase.getInstance(context)
    private val listingDao = database.listingDao()
    private val favoriteDao = database.favoriteDao()
    private val chatDao = database.chatDao()
    private val notificationDao = database.notificationDao()
    private val reviewDao = database.reviewDao()
    private val safetyReportDao = database.safetyReportDao()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val prefs = context.getSharedPreferences("localbazaar_prefs", Context.MODE_PRIVATE)

    // Current User Profile State
    private val _currentUser = MutableStateFlow(loadInitialUser())
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Current Selected Location
    private val _selectedLocation = MutableStateFlow(loadInitialLocation())
    val selectedLocation: StateFlow<SelectedLocation> = _selectedLocation.asStateFlow()

    // Filter state
    private val _currentFilter = MutableStateFlow(ListingFilter())
    val currentFilter: StateFlow<ListingFilter> = _currentFilter.asStateFlow()

    init {
        repositoryScope.launch {
            try {
                seedInitialDataIfEmpty()
            } catch (e: Throwable) {
                android.util.Log.e("MarketplaceRepo", "Initial seed error", e)
            }
        }

        // Attach Firebase Auth state listener to synchronize profile
        try {
            FirebaseAuth.getInstance().addAuthStateListener { auth ->
                try {
                    val fbUser = auth.currentUser
                    if (fbUser != null) {
                        val current = _currentUser.value
                        if (current.id != fbUser.uid) {
                            val name = fbUser.displayName ?: prefs.getString("user_name", null) ?: (if (!fbUser.phoneNumber.isNullOrBlank()) "User (${fbUser.phoneNumber?.takeLast(4)})" else "Local Member")
                            val email = fbUser.email ?: prefs.getString("user_email", "") ?: ""
                            val phone = fbUser.phoneNumber ?: prefs.getString("user_phone", "") ?: ""
                            val avatar = fbUser.photoUrl?.toString() ?: prefs.getString("user_avatar", "") ?: ""
                            val locState = prefs.getString("user_state", _selectedLocation.value.state) ?: _selectedLocation.value.state
                            val locDistrict = prefs.getString("user_district", _selectedLocation.value.district) ?: _selectedLocation.value.district
                            val locArea = prefs.getString("user_area", _selectedLocation.value.area) ?: _selectedLocation.value.area

                            val updated = UserProfile(
                                id = fbUser.uid,
                                name = name,
                                email = email,
                                phone = phone,
                                avatarUrl = avatar,
                                state = locState,
                                district = locDistrict,
                                area = locArea,
                                joinDate = "Active Member",
                                verificationBadge = if (phone.isNotBlank()) SellerVerification.PHONE_VERIFIED else SellerVerification.EMAIL_VERIFIED,
                                rating = 5.0,
                                reviewCount = 0,
                                aboutBio = "Verified LocalBazaar member."
                            )
                            _currentUser.value = updated
                        }
                    }
                } catch (e: Throwable) {
                    android.util.Log.e("MarketplaceRepo", "Auth listener error", e)
                }
            }
        } catch (_: Throwable) {}
    }

    private fun loadInitialLocation(): SelectedLocation {
        val st = prefs.getString("loc_state", "Maharashtra") ?: "Maharashtra"
        val dist = prefs.getString("loc_dist", "Mumbai Suburban") ?: "Mumbai Suburban"
        val ar = prefs.getString("loc_area", "Bandra West") ?: "Bandra West"
        return SelectedLocation(state = st, district = dist, area = ar)
    }

    private fun loadInitialUser(): UserProfile {
        try {
            val fbUser = FirebaseAuth.getInstance().currentUser
            if (fbUser != null) {
                return UserProfile(
                    id = fbUser.uid,
                    name = fbUser.displayName ?: (if (!fbUser.phoneNumber.isNullOrBlank()) "User (${fbUser.phoneNumber?.takeLast(4)})" else "Local Member"),
                    phone = fbUser.phoneNumber ?: "",
                    email = fbUser.email ?: "",
                    avatarUrl = fbUser.photoUrl?.toString() ?: "",
                    state = prefs.getString("loc_state", "Maharashtra") ?: "Maharashtra",
                    district = prefs.getString("loc_dist", "Mumbai Suburban") ?: "Mumbai Suburban",
                    area = prefs.getString("loc_area", "Bandra West") ?: "Bandra West",
                    joinDate = "Active Member",
                    verificationBadge = if (!fbUser.phoneNumber.isNullOrBlank()) SellerVerification.PHONE_VERIFIED else SellerVerification.EMAIL_VERIFIED,
                    rating = 5.0,
                    reviewCount = 0,
                    aboutBio = "Verified LocalBazaar community member."
                )
            }
        } catch (_: Exception) {}

        return UserProfile(
            id = "usr_current_me",
            name = "Aarav Sharma",
            phone = "+91 98765 43210",
            email = "aarav.sharma@example.in",
            state = "Maharashtra",
            district = "Mumbai Suburban",
            area = "Bandra West",
            joinDate = "March 2024",
            verificationBadge = SellerVerification.VERIFIED_SELLER,
            rating = 4.9,
            reviewCount = 18,
            aboutBio = "Active buyer & seller in Bandra & Mumbai Suburban. Fast responder!"
        )
    }

    fun updateSelectedLocation(state: String, district: String, area: String) {
        _selectedLocation.value = SelectedLocation(state = state, district = district, area = area)
        prefs.edit()
            .putString("loc_state", state)
            .putString("loc_dist", district)
            .putString("loc_area", area)
            .apply()
    }

    fun updateFilter(filter: ListingFilter) {
        _currentFilter.value = filter
    }

    fun clearFilters() {
        _currentFilter.value = ListingFilter()
    }

    fun setUserProfile(profile: UserProfile) {
        _currentUser.value = profile
        prefs.edit()
            .putString("user_name", profile.name)
            .putString("user_email", profile.email)
            .putString("user_phone", profile.phone)
            .putString("user_avatar", profile.avatarUrl)
            .putString("user_state", profile.state)
            .putString("user_district", profile.district)
            .putString("user_area", profile.area)
            .apply()

        // Keep selected location in sync if profile has location
        if (profile.state.isNotBlank() && profile.district.isNotBlank() && profile.area.isNotBlank()) {
            updateSelectedLocation(profile.state, profile.district, profile.area)
        }
    }

    fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (_: Exception) {}

        prefs.edit()
            .remove("user_name")
            .remove("user_email")
            .remove("user_phone")
            .remove("user_avatar")
            .remove("user_bio")
            .remove("is_business")
            .remove("business_name")
            .remove("business_cat")
            .apply()

        _currentUser.value = UserProfile(
            id = "usr_guest_" + UUID.randomUUID().toString().take(6),
            name = "Guest User",
            phone = "",
            email = "",
            state = _selectedLocation.value.state,
            district = _selectedLocation.value.district,
            area = _selectedLocation.value.area,
            joinDate = "Today",
            verificationBadge = SellerVerification.NONE,
            rating = 5.0,
            reviewCount = 0,
            aboutBio = "LocalBazaar community member."
        )
    }

    fun updateProfile(name: String, bio: String, state: String, district: String, area: String, isBusiness: Boolean, businessName: String?, businessCat: String?) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            aboutBio = bio,
            state = state,
            district = district,
            area = area,
            isBusiness = isBusiness,
            businessName = businessName,
            businessCategory = businessCat,
            verificationBadge = if (isBusiness) SellerVerification.VERIFIED_BUSINESS else _currentUser.value.verificationBadge
        )
    }

    // Listings Flow combined with favorites
    fun getFilteredListings(): Flow<List<ListingItem>> {
        return combine(
            listingDao.getAllListings(),
            favoriteDao.getFavoriteIds(),
            _selectedLocation,
            _currentFilter
        ) { entityList, favIds, loc, filter ->
            val favSet = favIds.toSet()
            entityList
                .map { it.toListingItem(isFav = favSet.contains(it.id)) }
                .filter { item ->
                    // Only active listings in main browse (or allow filter to check status)
                    item.status == ListingStatus.ACTIVE
                }
                .filter { item ->
                    // Filter by location if specified, or default to matching district/state when location filter active
                    val matchesState = filter.state?.let { item.state.equals(it, ignoreCase = true) } ?: true
                    val matchesDistrict = filter.district?.let { item.district.equals(it, ignoreCase = true) } ?: true
                    val matchesArea = filter.area?.let { item.area.equals(it, ignoreCase = true) } ?: true
                    matchesState && matchesDistrict && matchesArea
                }
                .filter { item ->
                    // Filter by keyword
                    if (filter.keyword.isNotBlank()) {
                        val q = filter.keyword.trim().lowercase()
                        item.title.lowercase().contains(q) ||
                                item.description.lowercase().contains(q) ||
                                item.category.lowercase().contains(q) ||
                                item.area.lowercase().contains(q)
                    } else true
                }
                .filter { item ->
                    // Category filter
                    if (filter.category != null) {
                        item.category.equals(filter.category, ignoreCase = true)
                    } else true
                }
                .filter { item ->
                    // Service filter
                    if (filter.isService != null) {
                        item.isService == filter.isService
                    } else true
                }
                .filter { item ->
                    // Price range
                    val minOk = filter.minPrice?.let { item.price >= it } ?: true
                    val maxOk = filter.maxPrice?.let { item.price <= it } ?: true
                    minOk && maxOk
                }
                .filter { item ->
                    // Condition
                    if (filter.condition != null) {
                        item.condition == filter.condition
                    } else true
                }
                .filter { item ->
                    // Verified only
                    if (filter.verifiedOnly) {
                        item.sellerBadge != SellerVerification.NONE
                    } else true
                }
                .filter { item ->
                    // Featured only
                    if (filter.featuredOnly) {
                        item.isFeatured || item.isBoosted
                    } else true
                }
                .sortedWith(
                    when (filter.sortOption) {
                        SortOption.NEWEST -> compareByDescending { it.postedTimestamp }
                        SortOption.PRICE_LOW_HIGH -> compareBy { it.price }
                        SortOption.PRICE_HIGH_LOW -> compareByDescending { it.price }
                        SortOption.POPULAR -> compareByDescending { it.viewsCount + (it.favoritesCount * 3) }
                        SortOption.NEAREST -> compareBy {
                            if (it.area.equals(loc.area, ignoreCase = true)) 0
                            else if (it.district.equals(loc.district, ignoreCase = true)) 1
                            else if (it.state.equals(loc.state, ignoreCase = true)) 2
                            else 3
                        }
                    }
                )
        }
    }

    fun getListingById(id: String): Flow<ListingItem?> {
        return combine(
            listingDao.getListingById(id),
            favoriteDao.getFavoriteIds()
        ) { entity, favIds ->
            entity?.toListingItem(isFav = favIds.contains(entity.id))
        }
    }

    fun getMyListings(): Flow<List<ListingItem>> {
        val currentUserId = _currentUser.value.id
        return combine(
            listingDao.getListingsBySeller(currentUserId),
            favoriteDao.getFavoriteIds()
        ) { entities, favIds ->
            entities.map { it.toListingItem(isFav = favIds.contains(it.id)) }
        }
    }

    fun getSellerListings(sellerId: String): Flow<List<ListingItem>> {
        return combine(
            listingDao.getListingsBySeller(sellerId),
            favoriteDao.getFavoriteIds()
        ) { entities, favIds ->
            entities.filter { it.status == ListingStatus.ACTIVE.name }
                .map { it.toListingItem(isFav = favIds.contains(it.id)) }
        }
    }

    fun getFavoriteListings(): Flow<List<ListingItem>> {
        return combine(
            listingDao.getAllListings(),
            favoriteDao.getFavoriteIds()
        ) { entities, favIds ->
            val favSet = favIds.toSet()
            entities.filter { favSet.contains(it.id) }
                .map { it.toListingItem(isFav = true) }
        }
    }

    fun getPendingReviewListings(): Flow<List<ListingItem>> {
        return listingDao.getPendingReviewListings().map { list ->
            list.map { it.toListingItem() }
        }
    }

    suspend fun toggleFavorite(listingId: String) {
        val isFav = favoriteDao.getFavoriteIds().first().contains(listingId)
        if (isFav) {
            favoriteDao.removeFavorite(listingId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(listingId))
        }
    }

    suspend fun createListing(
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
        isDraft: Boolean = false
    ): String {
        val user = _currentUser.value
        val id = "lst_" + UUID.randomUUID().toString().take(8)
        val initialStatus = if (isDraft) ListingStatus.DRAFT else ListingStatus.PENDING_REVIEW

        val entity = ListingEntity(
            id = id,
            title = title,
            description = description,
            price = price,
            isNegotiable = isNegotiable,
            category = category,
            subcategory = subcategory,
            isService = isService,
            imagesCsv = images.joinToString("|"),
            condition = condition.name,
            state = state,
            district = district,
            area = area,
            sellerId = user.id,
            sellerName = user.name,
            sellerAvatar = user.avatarUrl,
            sellerRating = user.rating,
            sellerReviewCount = user.reviewCount,
            sellerBadge = user.verificationBadge.name,
            postedTimestamp = System.currentTimeMillis(),
            isFeatured = false,
            isBoosted = false,
            status = initialStatus.name,
            rejectionReason = null,
            viewsCount = 1,
            favoritesCount = 0,
            businessName = if (user.isBusiness) user.businessName else null
        )

        listingDao.insertListing(entity)

        if (!isDraft) {
            // Add notification for submission
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    title = "Listing Under Review",
                    message = "Your listing \"$title\" has been submitted for moderation. You will be notified once active.",
                    type = NotificationType.APPROVAL.name,
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    referenceId = id
                )
            )
        }

        return id
    }

    suspend fun updateListing(listing: ListingItem) {
        listingDao.updateListing(ListingEntity.fromListingItem(listing))
    }

    suspend fun deleteListing(id: String) {
        listingDao.deleteListing(id)
    }

    suspend fun markAsSold(id: String) {
        listingDao.updateListingStatus(id, ListingStatus.SOLD.name)
    }

    suspend fun toggleBoost(id: String, isBoosted: Boolean) {
        listingDao.updateBoostStatus(id, isBoosted)
    }

    // Admin Moderation & User Management
    private fun loadBlockedUserIds(): Set<String> {
        return prefs.getStringSet("admin_blocked_user_ids", emptySet()) ?: emptySet()
    }

    private val _blockedUserIds = MutableStateFlow(loadBlockedUserIds())
    val blockedUserIds: StateFlow<Set<String>> = _blockedUserIds.asStateFlow()

    fun toggleBlockUser(userId: String, isBlocked: Boolean) {
        val current = _blockedUserIds.value.toMutableSet()
        if (isBlocked) {
            current.add(userId)
        } else {
            current.remove(userId)
        }
        _blockedUserIds.value = current
        prefs.edit().putStringSet("admin_blocked_user_ids", current).apply()
    }

    fun getAllListings(): Flow<List<ListingItem>> {
        return combine(listingDao.getAllListings(), favoriteDao.getFavoriteIds()) { entities, favIds ->
            entities.map { it.toListingItem(isFav = favIds.contains(it.id)) }
        }
    }

    suspend fun setListingStatus(id: String, status: ListingStatus, reason: String? = null) {
        listingDao.updateListingStatus(id, status.name, reason)
    }

    fun getAdminUsers(): Flow<List<AdminUserInfo>> {
        return combine(listingDao.getAllListings(), _currentUser, _blockedUserIds) { listings, currentMe, blockedIds ->
            val userMap = mutableMapOf<String, AdminUserInfo>()

            userMap[currentMe.id] = AdminUserInfo(
                userId = currentMe.id,
                name = currentMe.name + " (Current Account)",
                phone = currentMe.phone.ifBlank { "+91 98765 43210" },
                email = currentMe.email,
                location = "${currentMe.area}, ${currentMe.district}",
                listingsCount = listings.count { it.sellerId == currentMe.id },
                isBlocked = blockedIds.contains(currentMe.id),
                verificationBadge = currentMe.verificationBadge
            )

            listings.forEach { entity ->
                if (!userMap.containsKey(entity.sellerId)) {
                    val badge = try {
                        SellerVerification.valueOf(entity.sellerBadge)
                    } catch (_: Exception) {
                        SellerVerification.VERIFIED_SELLER
                    }
                    userMap[entity.sellerId] = AdminUserInfo(
                        userId = entity.sellerId,
                        name = entity.sellerName,
                        phone = getSamplePhoneForUser(entity.sellerId),
                        location = "${entity.area}, ${entity.district}",
                        listingsCount = listings.count { it.sellerId == entity.sellerId },
                        isBlocked = blockedIds.contains(entity.sellerId),
                        verificationBadge = badge
                    )
                }
            }
            userMap.values.toList()
        }
    }

    private fun getSamplePhoneForUser(userId: String): String {
        return when (userId) {
            "usr_rohit_m" -> "+91 98201 45678"
            "usr_priya_s" -> "+91 98332 98765"
            "usr_amit_v" -> "+91 91672 34567"
            "usr_neha_k" -> "+91 99203 12345"
            "usr_vikram_p" -> "+91 98450 87654"
            "usr_ananya_b" -> "+91 97401 23456"
            "usr_rahul_d" -> "+91 98110 56789"
            "usr_pooja_n" -> "+91 99100 43210"
            else -> "+91 98765 ${Math.abs(userId.hashCode() % 90000 + 10000)}"
        }
    }

    suspend fun approveListing(id: String) {
        listingDao.updateListingStatus(id, ListingStatus.ACTIVE.name, null)
        val listing = listingDao.getListingDirect(id)
        if (listing != null) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    title = "Listing Approved!",
                    message = "Great news! Your listing \"${listing.title}\" is now Live on LocalBazaar.",
                    type = NotificationType.APPROVAL.name,
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    referenceId = id
                )
            )
        }
    }

    suspend fun rejectListing(id: String, reason: String) {
        listingDao.updateListingStatus(id, ListingStatus.REJECTED.name, reason)
        val listing = listingDao.getListingDirect(id)
        if (listing != null) {
            notificationDao.insertNotification(
                NotificationEntity(
                    id = "notif_" + UUID.randomUUID().toString().take(8),
                    title = "Listing Needs Changes",
                    message = "Your listing \"${listing.title}\" was rejected: $reason. Please edit and resubmit.",
                    type = NotificationType.REJECTION.name,
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    referenceId = id
                )
            )
        }
    }

    // Chat methods
    fun getConversations(): Flow<List<Conversation>> {
        return chatDao.getConversations().map { list ->
            list.map { entity ->
                Conversation(
                    id = entity.id,
                    listingId = entity.listingId,
                    listingTitle = entity.listingTitle,
                    listingPrice = entity.listingPrice,
                    listingImage = entity.listingImage,
                    listingArea = entity.listingArea,
                    buyerId = entity.buyerId,
                    buyerName = entity.buyerName,
                    sellerId = entity.sellerId,
                    sellerName = entity.sellerName,
                    lastMessage = entity.lastMessage,
                    lastTimestamp = entity.lastTimestamp,
                    unreadCount = entity.unreadCount,
                    isBlocked = entity.isBlocked
                )
            }
        }
    }

    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForConversation(convId).map { list ->
            list.map { entity ->
                ChatMessage(
                    id = entity.id,
                    conversationId = entity.conversationId,
                    senderId = entity.senderId,
                    senderName = entity.senderName,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    isRead = entity.isRead,
                    isSystemNotice = entity.isSystemNotice
                )
            }
        }
    }

    suspend fun getOrCreateConversation(listing: ListingItem): String {
        val user = _currentUser.value
        val existing = chatDao.findConversationByListingAndBuyer(listing.id, user.id)
        if (existing != null) {
            return existing.id
        }

        val convId = "conv_" + UUID.randomUUID().toString().take(8)
        val newConv = ConversationEntity(
            id = convId,
            listingId = listing.id,
            listingTitle = listing.title,
            listingPrice = listing.price,
            listingImage = listing.images.firstOrNull() ?: "",
            listingArea = listing.area,
            buyerId = user.id,
            buyerName = user.name,
            sellerId = listing.sellerId,
            sellerName = listing.sellerName,
            lastMessage = "Hi, is this ${listing.title} still available?",
            lastTimestamp = System.currentTimeMillis(),
            unreadCount = 0,
            isBlocked = false
        )
        chatDao.insertConversation(newConv)

        // Add initial system security notice and initial greeting
        val sysMsg = MessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = convId,
            senderId = "system",
            senderName = "LocalBazaar Safety",
            text = "🛡️ Safety Notice: Never share OTPs, passwords, or pay advance tokens. Meet in a public place within ${listing.area}.",
            timestamp = System.currentTimeMillis() - 1000,
            isRead = true,
            isSystemNotice = true
        )
        chatDao.insertMessage(sysMsg)

        val firstMsg = MessageEntity(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            conversationId = convId,
            senderId = user.id,
            senderName = user.name,
            text = "Hi, is this ${listing.title} still available?",
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isSystemNotice = false
        )
        chatDao.insertMessage(firstMsg)

        return convId
    }

    suspend fun sendMessage(conversationId: String, text: String, targetUserId: String) {
        val user = _currentUser.value
        val msgId = "msg_" + UUID.randomUUID().toString().take(8)
        val msg = MessageEntity(
            id = msgId,
            conversationId = conversationId,
            senderId = user.id,
            senderName = user.name,
            text = text,
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isSystemNotice = false
        )
        chatDao.insertMessage(msg)

        val conv = chatDao.getConversationById(conversationId).first()
        if (conv != null) {
            chatDao.insertConversation(
                conv.copy(
                    lastMessage = text,
                    lastTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun blockUser(conversationId: String, blocked: Boolean) {
        chatDao.setBlocked(conversationId, blocked)
    }

    suspend fun markConversationRead(conversationId: String) {
        chatDao.markAsRead(conversationId)
    }

    // Safety & Reports
    suspend fun submitReport(targetType: String, targetId: String, reason: String, details: String) {
        val user = _currentUser.value
        val report = SafetyReportEntity(
            id = "rep_" + UUID.randomUUID().toString().take(8),
            targetType = targetType,
            targetId = targetId,
            reason = reason,
            details = details,
            reporterId = user.id,
            timestamp = System.currentTimeMillis(),
            status = "Under Admin Review"
        )
        safetyReportDao.insertReport(report)
    }

    // Notifications
    fun getNotifications(): Flow<List<AppNotification>> {
        return notificationDao.getNotifications().map { list ->
            list.map { entity ->
                val type = try {
                    NotificationType.valueOf(entity.type)
                } catch (e: Exception) {
                    NotificationType.MESSAGE
                }
                AppNotification(
                    id = entity.id,
                    title = entity.title,
                    message = entity.message,
                    type = type,
                    timestamp = entity.timestamp,
                    isRead = entity.isRead,
                    referenceId = entity.referenceId
                )
            }
        }
    }

    suspend fun markNotificationRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead() {
        notificationDao.markAllAsRead()
    }

    // Reviews
    fun getReviewsForUser(userId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForUser(userId).map { list ->
            list.map { entity ->
                Review(
                    id = entity.id,
                    listingId = entity.listingId,
                    listingTitle = entity.listingTitle,
                    reviewerId = entity.reviewerId,
                    reviewerName = entity.reviewerName,
                    reviewerAvatar = entity.reviewerAvatar,
                    targetUserId = entity.targetUserId,
                    rating = entity.rating,
                    comment = entity.comment,
                    timestamp = entity.timestamp,
                    isReported = entity.isReported
                )
            }
        }
    }

    suspend fun addReview(listingId: String, listingTitle: String, targetUserId: String, rating: Int, comment: String) {
        val user = _currentUser.value
        val rev = ReviewEntity(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            listingId = listingId,
            listingTitle = listingTitle,
            reviewerId = user.id,
            reviewerName = user.name,
            reviewerAvatar = user.avatarUrl,
            targetUserId = targetUserId,
            rating = rating,
            comment = comment,
            timestamp = System.currentTimeMillis(),
            isReported = false
        )
        reviewDao.insertReview(rev)
    }

    // Seed Data
    private suspend fun seedInitialDataIfEmpty() {
        try {
            val existing = listingDao.getAllListings().firstOrNull() ?: emptyList()
            if (existing.isNotEmpty()) return

        val sampleListings = listOf(
            // --- MAHARASHTRA / MUMBAI & PUNE ---
            ListingEntity(
                id = "lst_mumbai_iphone",
                title = "iPhone 14 Pro 128GB - Deep Purple (Bill & Box)",
                description = "Original Indian purchase with invoice and box. Battery health 91%. 0 scratches, tempered glass and cover applied since day 1. Selling because upgraded to 16 Pro.",
                price = 54999.0,
                isNegotiable = true,
                category = "Mobiles & Tablets",
                subcategory = "iPhones",
                isService = false,
                imagesCsv = "localbazaar_hero|localbazaar_logo",
                condition = ItemCondition.LIKE_NEW.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Bandra West",
                sellerId = "usr_rohit_m",
                sellerName = "Rohit Mehta",
                sellerAvatar = "",
                sellerRating = 4.9,
                sellerReviewCount = 24,
                sellerBadge = SellerVerification.VERIFIED_SELLER.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 4,
                isFeatured = true,
                isBoosted = true,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 142,
                favoritesCount = 18,
                businessName = null
            ),
            ListingEntity(
                id = "lst_mumbai_macbook",
                title = "MacBook Air M2 (16GB RAM, 512GB SSD) Midnight",
                description = "Mint condition Apple MacBook Air M2. Used strictly for light coding and college work. Comes with Apple 35W dual charger and original packaging.",
                price = 78000.0,
                isNegotiable = false,
                category = "Laptops & Computers",
                subcategory = "MacBooks",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.LIKE_NEW.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Andheri West",
                sellerId = "usr_priya_k",
                sellerName = "Priya Kulkarni",
                sellerAvatar = "",
                sellerRating = 5.0,
                sellerReviewCount = 16,
                sellerBadge = SellerVerification.VERIFIED_SELLER.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 8,
                isFeatured = true,
                isBoosted = false,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 98,
                favoritesCount = 12,
                businessName = null
            ),
            ListingEntity(
                id = "lst_mumbai_royal_enfield",
                title = "Royal Enfield Hunter 350 Dapper Ash (2023 - 8,500 km)",
                description = "Single owner, Mumbai MH-02 registration. Fully maintained at authorized Royal Enfield service center with service logs. Crash guard and sump guard fitted.",
                price = 129000.0,
                isNegotiable = true,
                category = "Bikes & Scooters",
                subcategory = "Motorcycles",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.LIKE_NEW.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Bandra West",
                sellerId = "usr_vikram_s",
                sellerName = "Vikram Singh Motors",
                sellerAvatar = "",
                sellerRating = 4.8,
                sellerReviewCount = 42,
                sellerBadge = SellerVerification.VERIFIED_BUSINESS.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 12,
                isFeatured = true,
                isBoosted = true,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 310,
                favoritesCount = 45,
                businessName = "Vikram Pre-Owned Two Wheelers"
            ),
            ListingEntity(
                id = "lst_mumbai_sofa",
                title = "Solid Teak Wood 5-Seater L-Shape Sofa Set (Beige Fabric)",
                description = "Custom-made solid Sheesham/Teak base with high density foam cushions and washable covers. Only 1.5 years old, spotless condition. Relocating abroad.",
                price = 19500.0,
                isNegotiable = true,
                category = "Furniture",
                subcategory = "Sofas & Couches",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.GOOD.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Juhu",
                sellerId = "usr_ananya_d",
                sellerName = "Ananya Desai",
                sellerAvatar = "",
                sellerRating = 4.7,
                sellerReviewCount = 8,
                sellerBadge = SellerVerification.PHONE_VERIFIED.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 18,
                isFeatured = false,
                isBoosted = false,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 65,
                favoritesCount = 7,
                businessName = null
            ),
            ListingEntity(
                id = "lst_srv_mumbai_electrician",
                title = "Sharma Electricals - 24x7 Doorstep Electrician & Inverter Fix",
                description = "Certified senior electrician in Mumbai Suburban. Short circuit repairs, MCB box install, inverter installation, fan and LED chandelier fitting with fast 30-min response in Bandra & Andheri.",
                price = 299.0,
                isNegotiable = false,
                category = "Electricians",
                subcategory = "Wiring & Repair",
                isService = true,
                imagesCsv = "localbazaar_logo",
                condition = ItemCondition.NOT_APPLICABLE.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Bandra West",
                sellerId = "usr_sharma_elec",
                sellerName = "Mukesh Sharma (Master Electrician)",
                sellerAvatar = "",
                sellerRating = 4.9,
                sellerReviewCount = 89,
                sellerBadge = SellerVerification.VERIFIED_BUSINESS.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 2,
                isFeatured = true,
                isBoosted = true,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 520,
                favoritesCount = 64,
                businessName = "Sharma 24x7 Electrical Solutions"
            ),
            ListingEntity(
                id = "lst_srv_mumbai_tutor",
                title = "ICSE & CBSE Class 9-12 Maths & Physics Home Tutor",
                description = "M.Sc Physics with 9+ years experience teaching IIT-JEE foundation, CBSE & ICSE board toppers. Individual attention, weekly mock tests & doubt clearing sessions in Bandra, Santacruz & Khar.",
                price = 700.0,
                isNegotiable = true,
                category = "Tutors & Coaching",
                subcategory = "School Tutors",
                isService = true,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.NOT_APPLICABLE.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Bandra West",
                sellerId = "usr_prof_verma",
                sellerName = "Prof. Alok Verma",
                sellerAvatar = "",
                sellerRating = 5.0,
                sellerReviewCount = 31,
                sellerBadge = SellerVerification.VERIFIED_SELLER.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 24,
                isFeatured = false,
                isBoosted = false,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 180,
                favoritesCount = 28,
                businessName = null
            ),
            // --- DELHI NCR ---
            ListingEntity(
                id = "lst_delhi_sony_tv",
                title = "Sony Bravia 55-inch 4K Google TV (KD-55X74K)",
                description = "Ultra HD 4K LED Smart TV with Dolby Audio and built-in Chromecast. Impeccable display, no dead pixels. Wall mount bracket included. Bill available.",
                price = 36000.0,
                isNegotiable = true,
                category = "Electronics",
                subcategory = "TVs & Monitors",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.LIKE_NEW.name,
                state = "Delhi NCR",
                district = "South Delhi",
                area = "Hauz Khas",
                sellerId = "usr_karan_delhi",
                sellerName = "Karan Bhasin",
                sellerAvatar = "",
                sellerRating = 4.8,
                sellerReviewCount = 14,
                sellerBadge = SellerVerification.VERIFIED_SELLER.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 6,
                isFeatured = true,
                isBoosted = false,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 88,
                favoritesCount = 9,
                businessName = null
            ),
            ListingEntity(
                id = "lst_delhi_mobile_repair",
                title = "Express Mobile Screen & Motherboard Repair Hauz Khas",
                description = "Original AMOLED displays for iPhone, Samsung Galaxy, OnePlus and Pixel with 6 months warranty. Same-day turnaround and doorstep pickup available in South Delhi.",
                price = 499.0,
                isNegotiable = false,
                category = "Mobile & Laptop Repair",
                subcategory = "Screen Replacement",
                isService = true,
                imagesCsv = "localbazaar_logo",
                condition = ItemCondition.NOT_APPLICABLE.name,
                state = "Delhi NCR",
                district = "South Delhi",
                area = "Hauz Khas",
                sellerId = "usr_quick_fix_delhi",
                sellerName = "FixIt Quick Mobile Hub",
                sellerAvatar = "",
                sellerRating = 4.9,
                sellerReviewCount = 76,
                sellerBadge = SellerVerification.VERIFIED_BUSINESS.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 10,
                isFeatured = true,
                isBoosted = true,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 410,
                favoritesCount = 37,
                businessName = "FixIt Quick Electronics Hub"
            ),
            // --- KARNATAKA / BENGALURU ---
            ListingEntity(
                id = "lst_blr_standing_desk",
                title = "Ergonomic Motorized Height Adjustable Standing Desk (Teak Top)",
                description = "Dual motor electric standing desk with memory presets, cable management tray and heavy duty steel legs. Perfect for WFH. 140cm x 70cm.",
                price = 14500.0,
                isNegotiable = true,
                category = "Furniture",
                subcategory = "Office Chairs & Desks",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.LIKE_NEW.name,
                state = "Karnataka",
                district = "Bengaluru Urban",
                area = "Indiranagar",
                sellerId = "usr_siddharth_blr",
                sellerName = "Siddharth Rao",
                sellerAvatar = "",
                sellerRating = 4.9,
                sellerReviewCount = 20,
                sellerBadge = SellerVerification.VERIFIED_SELLER.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 3,
                isFeatured = true,
                isBoosted = true,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 215,
                favoritesCount = 26,
                businessName = null
            ),
            ListingEntity(
                id = "lst_blr_yamaha_guitar",
                title = "Yamaha F310 Acoustic Guitar with Padded Bag & Capo",
                description = "Genuine Yamaha acoustic dreadnought guitar with rich acoustic resonance. Strung with fresh D'Addario phosphor bronze strings. Includes heavy padded gig bag and tuner.",
                price = 6800.0,
                isNegotiable = true,
                category = "Musical Instruments",
                subcategory = "Acoustic & Electric Guitars",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.GOOD.name,
                state = "Karnataka",
                district = "Bengaluru Urban",
                area = "Koramangala",
                sellerId = "usr_kavita_blr",
                sellerName = "Kavita Menon",
                sellerAvatar = "",
                sellerRating = 4.7,
                sellerReviewCount = 5,
                sellerBadge = SellerVerification.PHONE_VERIFIED.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 15,
                isFeatured = false,
                isBoosted = false,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 74,
                favoritesCount = 8,
                businessName = null
            ),
            // --- TAMIL NADU / CHENNAI ---
            ListingEntity(
                id = "lst_chn_plumber",
                title = "Lakshmi Plumbing & Sanitary Solutions T. Nagar",
                description = "Expert residential & commercial plumbers. Leakage detection, PVC & CPVC piping, bathroom luxury fitting, pressure pumps and water heater maintenance.",
                price = 350.0,
                isNegotiable = false,
                category = "Plumbers",
                subcategory = "Bathroom Fittings",
                isService = true,
                imagesCsv = "localbazaar_logo",
                condition = ItemCondition.NOT_APPLICABLE.name,
                state = "Tamil Nadu",
                district = "Chennai",
                area = "T. Nagar",
                sellerId = "usr_lakshmi_plumb",
                sellerName = "K. Ramanathan",
                sellerAvatar = "",
                sellerRating = 4.8,
                sellerReviewCount = 52,
                sellerBadge = SellerVerification.VERIFIED_BUSINESS.name,
                postedTimestamp = System.currentTimeMillis() - 3600000 * 7,
                isFeatured = true,
                isBoosted = false,
                status = ListingStatus.ACTIVE.name,
                rejectionReason = null,
                viewsCount = 280,
                favoritesCount = 29,
                businessName = "Lakshmi Plumbing Works"
            ),
            // Moderation test sample (Pending Review)
            ListingEntity(
                id = "lst_pending_demo",
                title = "OnePlus Nord CE 3 5G (8GB/128GB) Aqua Surge",
                description = "Brand new box packed with sealed warranty. Unwanted festival gift.",
                price = 18500.0,
                isNegotiable = true,
                category = "Mobiles & Tablets",
                subcategory = "Smartphones",
                isService = false,
                imagesCsv = "localbazaar_hero",
                condition = ItemCondition.BRAND_NEW.name,
                state = "Maharashtra",
                district = "Mumbai Suburban",
                area = "Bandra West",
                sellerId = "usr_current_me",
                sellerName = "Aarav Sharma",
                sellerAvatar = "",
                sellerRating = 4.9,
                sellerReviewCount = 18,
                sellerBadge = SellerVerification.VERIFIED_SELLER.name,
                postedTimestamp = System.currentTimeMillis() - 1800000,
                isFeatured = false,
                isBoosted = false,
                status = ListingStatus.PENDING_REVIEW.name,
                rejectionReason = null,
                viewsCount = 2,
                favoritesCount = 0,
                businessName = null
            )
        )

        listingDao.insertListings(sampleListings)

        // Seed Sample Conversation
        val convId = "conv_sample_1"
        val sampleConv = ConversationEntity(
            id = convId,
            listingId = "lst_mumbai_iphone",
            listingTitle = "iPhone 14 Pro 128GB - Deep Purple (Bill & Box)",
            listingPrice = 54999.0,
            listingImage = "localbazaar_hero",
            listingArea = "Bandra West",
            buyerId = "usr_current_me",
            buyerName = "Aarav Sharma",
            sellerId = "usr_rohit_m",
            sellerName = "Rohit Mehta",
            lastMessage = "Yes, bill and box are available. Can we meet at Hill Road Starbucks?",
            lastTimestamp = System.currentTimeMillis() - 1800000,
            unreadCount = 1,
            isBlocked = false
        )
        chatDao.insertConversation(sampleConv)

        chatDao.insertMessage(
            MessageEntity(
                id = "msg_seed_1",
                conversationId = convId,
                senderId = "system",
                senderName = "LocalBazaar Safety",
                text = "🛡️ Safety Tip: Meet in daylight at a public place. Inspect the phone and test IMEI before making any payment.",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = true,
                isSystemNotice = true
            )
        )
        chatDao.insertMessage(
            MessageEntity(
                id = "msg_seed_2",
                conversationId = convId,
                senderId = "usr_current_me",
                senderName = "Aarav Sharma",
                text = "Hi Rohit, is this iPhone 14 Pro still available? Is the bill from Apple BKC?",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true,
                isSystemNotice = false
            )
        )
        chatDao.insertMessage(
            MessageEntity(
                id = "msg_seed_3",
                conversationId = convId,
                senderId = "usr_rohit_m",
                senderName = "Rohit Mehta",
                text = "Yes, bill and box are available. Can we meet at Hill Road Starbucks?",
                timestamp = System.currentTimeMillis() - 1800000,
                isRead = false,
                isSystemNotice = false
            )
        )

        // Seed Notifications
        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_seed_welcome",
                title = "Welcome to LocalBazaar!",
                message = "Explore verified local buyers, sellers, and services across Bandra & Mumbai Suburban.",
                type = NotificationType.APPROVAL.name,
                timestamp = System.currentTimeMillis() - 86400000,
                isRead = true,
                referenceId = null
            )
        )
        notificationDao.insertNotification(
            NotificationEntity(
                id = "notif_seed_safety",
                title = "LocalBazaar Safety Tips",
                message = "Never transfer advance booking fees or scan payment QR codes. Always test items in person.",
                type = NotificationType.SECURITY.name,
                timestamp = System.currentTimeMillis() - 43200000,
                isRead = false,
                referenceId = null
            )
        )
        } catch (e: Throwable) {
            android.util.Log.e("MarketplaceRepo", "Error inserting seed data", e)
        }
    }
}
