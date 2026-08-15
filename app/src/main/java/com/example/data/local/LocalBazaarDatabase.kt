package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
  entities = [
    ProductEntity::class,
    StoreEntity::class,
    CartItemEntity::class,
    OrderEntity::class,
    CommunityRequestEntity::class,
    MarketRateEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class LocalBazaarDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun storeDao(): StoreDao
  abstract fun cartDao(): CartDao
  abstract fun orderDao(): OrderDao
  abstract fun communityRequestDao(): CommunityRequestDao
  abstract fun marketRateDao(): MarketRateDao
}
