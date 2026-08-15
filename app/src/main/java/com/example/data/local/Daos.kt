package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY id ASC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getProductById(id: Int): ProductEntity?

  @Query("SELECT * FROM products WHERE category = :category ORDER BY id ASC")
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isFarmerDirect = 1 ORDER BY id ASC")
  fun getFarmerDirectProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isCustomAdded = 1 ORDER BY id DESC")
  fun getSellerProducts(): Flow<List<ProductEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProducts(products: List<ProductEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity): Long

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Query("UPDATE products SET inStock = :inStock WHERE id = :productId")
  suspend fun updateStock(productId: Int, inStock: Boolean)

  @Query("DELETE FROM products WHERE id = :productId")
  suspend fun deleteProduct(productId: Int)

  @Query("DELETE FROM products WHERE isCustomAdded = 0")
  suspend fun deleteAllDefault()

  @Query("DELETE FROM products")
  suspend fun deleteAll()

  @Query("SELECT COUNT(*) FROM products")
  suspend fun getCount(): Int
}

@Dao
interface StoreDao {
  @Query("SELECT * FROM stores ORDER BY distanceKm ASC")
  fun getAllStores(): Flow<List<StoreEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStores(stores: List<StoreEntity>)

  @Query("SELECT COUNT(*) FROM stores")
  suspend fun getCount(): Int
}

@Dao
interface CartDao {
  @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
  fun getAllCartItems(): Flow<List<CartItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(item: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE productId = :productId")
  suspend fun removeCartItem(productId: Int)

  @Query("DELETE FROM cart_items")
  suspend fun clearCart()
}

@Dao
interface OrderDao {
  @Query("SELECT * FROM orders ORDER BY placedAt DESC")
  fun getAllOrders(): Flow<List<OrderEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity)

  @Query("UPDATE orders SET orderStatus = :status WHERE orderId = :orderId")
  suspend fun updateOrderStatus(orderId: String, status: String)

  @Query("SELECT * FROM orders WHERE orderId = :orderId")
  suspend fun getOrderById(orderId: String): OrderEntity?
}

@Dao
interface CommunityRequestDao {
  @Query("SELECT * FROM community_requests ORDER BY createdAt DESC")
  fun getAllRequests(): Flow<List<CommunityRequestEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRequest(request: CommunityRequestEntity)

  @Query("UPDATE community_requests SET offersCount = offersCount + 1 WHERE id = :id")
  suspend fun incrementOffers(id: Int)

  @Query("UPDATE community_requests SET status = :status WHERE id = :id")
  suspend fun updateStatus(id: Int, status: String)

  @Query("SELECT COUNT(*) FROM community_requests")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRequests(requests: List<CommunityRequestEntity>)
}

@Dao
interface MarketRateDao {
  @Query("SELECT * FROM market_rates ORDER BY id ASC")
  fun getAllRates(): Flow<List<MarketRateEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRates(rates: List<MarketRateEntity>)

  @Query("SELECT COUNT(*) FROM market_rates")
  suspend fun getCount(): Int
}
