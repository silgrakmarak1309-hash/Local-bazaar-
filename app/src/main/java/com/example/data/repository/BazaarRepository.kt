package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.CartItemEntity
import com.example.data.local.CommunityRequestEntity
import com.example.data.local.LocalBazaarDatabase
import com.example.data.local.MarketRateEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.SeedData
import com.example.data.local.StoreEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class BazaarRepository private constructor(private val database: LocalBazaarDatabase) {

  private val productDao = database.productDao()
  private val storeDao = database.storeDao()
  private val cartDao = database.cartDao()
  private val orderDao = database.orderDao()
  private val communityRequestDao = database.communityRequestDao()
  private val marketRateDao = database.marketRateDao()

  val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
  val allStores: Flow<List<StoreEntity>> = storeDao.getAllStores()
  val cartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()
  val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
  val communityRequests: Flow<List<CommunityRequestEntity>> = communityRequestDao.getAllRequests()
  val marketRates: Flow<List<MarketRateEntity>> = marketRateDao.getAllRates()

  fun initSeedData(scope: CoroutineScope) {
    scope.launch(Dispatchers.IO) {
      if (productDao.getCount() < SeedData.initialProducts.size) {
        productDao.deleteAll()
        productDao.insertProducts(SeedData.initialProducts)
      }
      if (storeDao.getCount() == 0) {
        storeDao.insertStores(SeedData.initialStores)
      }
      if (communityRequestDao.getCount() == 0) {
        communityRequestDao.insertRequests(SeedData.initialRequests)
      }
      if (marketRateDao.getCount() == 0) {
        marketRateDao.insertRates(SeedData.initialMarketRates)
      }
    }
  }

  suspend fun addToCart(productId: Int) {
    // Check if item already in cart
    cartDao.insertOrUpdate(
      CartItemEntity(
        productId = productId,
        quantity = 1,
        addedAt = System.currentTimeMillis()
      )
    )
  }

  suspend fun updateCartQuantity(productId: Int, quantity: Int) {
    if (quantity <= 0) {
      cartDao.removeCartItem(productId)
    } else {
      cartDao.insertOrUpdate(
        CartItemEntity(
          productId = productId,
          quantity = quantity,
          addedAt = System.currentTimeMillis()
        )
      )
    }
  }

  suspend fun removeFromCart(productId: Int) {
    cartDao.removeCartItem(productId)
  }

  suspend fun clearCart() {
    cartDao.clearCart()
  }

  suspend fun placeOrder(
    itemsSummary: String,
    totalAmount: Double,
    itemCount: Int,
    deliveryAddress: String,
    deliveryInstructions: String,
    paymentMethod: String,
    vendorName: String
  ): String {
    val randomSuffix = (1000..9999).random()
    val orderId = "LB-$randomSuffix"
    val order = OrderEntity(
      orderId = orderId,
      itemsSummary = itemsSummary,
      totalAmount = totalAmount,
      itemCount = itemCount,
      orderStatus = "CONFIRMED",
      deliveryAddress = deliveryAddress,
      deliveryInstructions = deliveryInstructions,
      paymentMethod = paymentMethod,
      placedAt = System.currentTimeMillis(),
      vendorName = vendorName,
      deliveryTimeEstimate = "15-30 mins"
    )
    orderDao.insertOrder(order)
    cartDao.clearCart()
    return orderId
  }

  suspend fun updateOrderStatus(orderId: String, status: String) {
    orderDao.updateOrderStatus(orderId, status)
  }

  suspend fun addNewProduct(product: ProductEntity): Long {
    return productDao.insertProduct(product)
  }

  suspend fun updateProductStock(productId: Int, inStock: Boolean) {
    productDao.updateStock(productId, inStock)
  }

  suspend fun deleteProduct(productId: Int) {
    productDao.deleteProduct(productId)
  }

  suspend fun postCommunityRequest(
    title: String,
    description: String,
    category: String,
    requesterName: String,
    locality: String
  ) {
    communityRequestDao.insertRequest(
      CommunityRequestEntity(
        title = title,
        description = description,
        category = category,
        requesterName = requesterName,
        locality = locality,
        status = "OPEN",
        offersCount = 0
      )
    )
  }

  suspend fun offerFulfillment(requestId: Int) {
    communityRequestDao.incrementOffers(requestId)
  }

  companion object {
    @Volatile
    private var INSTANCE: BazaarRepository? = null

    fun getInstance(context: Context): BazaarRepository {
      return INSTANCE ?: synchronized(this) {
        val database = Room.databaseBuilder(
          context.applicationContext,
          LocalBazaarDatabase::class.java,
          "local_bazaar.db"
        ).build()
        val repo = BazaarRepository(database)
        INSTANCE = repo
        repo
      }
    }
  }
}
