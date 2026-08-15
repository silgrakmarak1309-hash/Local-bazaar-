package com.example.model

enum class ListingStatus(val label: String) {
    DRAFT("Draft"),
    PENDING_REVIEW("Pending Review"),
    ACTIVE("Active"),
    SOLD("Sold"),
    REJECTED("Rejected"),
    REMOVED("Removed"),
    EXPIRED("Expired")
}

enum class ItemCondition(val label: String) {
    BRAND_NEW("Brand New"),
    LIKE_NEW("Like New"),
    GOOD("Good"),
    FAIR("Fair"),
    NOT_APPLICABLE("N/A (Service)")
}

enum class SellerVerification(val label: String) {
    NONE("Unverified"),
    PHONE_VERIFIED("Phone Verified"),
    EMAIL_VERIFIED("Email Verified"),
    VERIFIED_SELLER("Verified Seller"),
    VERIFIED_BUSINESS("Verified Business")
}

data class ListingItem(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val isNegotiable: Boolean = true,
    val category: String,
    val subcategory: String = "",
    val isService: Boolean = false,
    val images: List<String> = emptyList(), // image drawables or URIs
    val condition: ItemCondition = ItemCondition.GOOD,
    val state: String,
    val district: String,
    val area: String,
    val sellerId: String,
    val sellerName: String,
    val sellerAvatar: String = "",
    val sellerRating: Double = 4.8,
    val sellerReviewCount: Int = 12,
    val sellerBadge: SellerVerification = SellerVerification.VERIFIED_SELLER,
    val postedTimestamp: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val isBoosted: Boolean = false,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val rejectionReason: String? = null,
    val viewsCount: Int = 45,
    val favoritesCount: Int = 3,
    val isFavorite: Boolean = false,
    val businessName: String? = null
) {
    val formattedPrice: String get() = "₹%,d".format(price.toLong())
    val locationDisplay: String get() = "$area, $district"
    val fullLocationDisplay: String get() = "$area, $district, $state"
}

enum class SortOption(val label: String) {
    NEWEST("Newest First"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    POPULAR("Most Popular"),
    NEAREST("Nearest to My Area")
}

data class ListingFilter(
    val keyword: String = "",
    val category: String? = null,
    val isService: Boolean? = null,
    val state: String? = null,
    val district: String? = null,
    val area: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val condition: ItemCondition? = null,
    val verifiedOnly: Boolean = false,
    val featuredOnly: Boolean = false,
    val sortOption: SortOption = SortOption.NEWEST
)
