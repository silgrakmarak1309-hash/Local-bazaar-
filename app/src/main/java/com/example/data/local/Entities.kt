package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val category: String,
  val price: Double,
  val originalPrice: Double,
  val unit: String,
  val vendorName: String,
  val vendorLocality: String,
  val vendorPhone: String,
  val distanceKm: Double,
  val rating: Double,
  val reviewCount: Int,
  val description: String,
  val inStock: Boolean = true,
  val isFarmerDirect: Boolean = false,
  val badge: String = "",
  val harvestOrPackDate: String = "Fresh Batch Today",
  val isCustomAdded: Boolean = false,
  val iconEmoji: String = "🛒"
)

@Entity(tableName = "stores")
data class StoreEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val name: String,
  val category: String,
  val locality: String,
  val address: String,
  val phone: String,
  val rating: Double,
  val distanceKm: Double,
  val deliveryTime: String,
  val openingHours: String,
  val isOpen: Boolean = true,
  val isVerified: Boolean = true,
  val storeBadge: String = "Local Favorite",
  val storeEmoji: String = "🏪"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey val productId: Int,
  val quantity: Int,
  val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey val orderId: String,
  val itemsSummary: String,
  val totalAmount: Double,
  val itemCount: Int,
  val orderStatus: String, // "CONFIRMED", "PACKED", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"
  val deliveryAddress: String,
  val deliveryInstructions: String,
  val paymentMethod: String,
  val placedAt: Long = System.currentTimeMillis(),
  val vendorName: String,
  val deliveryTimeEstimate: String = "20-30 mins"
)

@Entity(tableName = "community_requests")
data class CommunityRequestEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val description: String,
  val category: String,
  val requesterName: String,
  val locality: String,
  val status: String = "OPEN", // "OPEN", "FULFILLED"
  val offersCount: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "market_rates")
data class MarketRateEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val commodity: String,
  val hindiName: String,
  val mandiPrice: String,
  val trend: String, // "UP", "DOWN", "STABLE"
  val trendPercentage: String,
  val updatedTime: String = "Today, 7:00 AM",
  val emoji: String = "🌾"
)
