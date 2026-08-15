package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CartItemEntity
import com.example.data.local.CommunityRequestEntity
import com.example.data.local.MarketRateEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.StoreEntity
import com.example.data.repository.BazaarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
  HOME("Home"),
  CATEGORIES("Categories"),
  CHATS("Chats"),
  PROFILE("Profile")
}

data class UserAccount(
  val name: String,
  val email: String,
  val role: String = "BUYER",
  val isAdmin: Boolean = false,
  val avatarInitial: String = "S",
  val avatarColorHex: Long = 0xFF0284C7
)

data class UserRecord(
  val id: String,
  val name: String,
  val email: String,
  val phone: String,
  val role: String,
  val isBlocked: Boolean = false,
  val joinedDate: String = "Aug 2026",
  val avatarEmoji: String = "👤"
)

data class ChatMessage(
  val id: String,
  val senderName: String,
  val text: String,
  val time: String,
  val isMe: Boolean
)

data class ChatThread(
  val id: String,
  val contactName: String,
  val contactSubtitle: String,
  val lastMessage: String,
  val time: String,
  val unreadCount: Int = 0,
  val avatarEmoji: String = "🏪",
  val messages: List<ChatMessage> = emptyList()
)

data class CartItemWithProduct(
  val cartItem: CartItemEntity,
  val product: ProductEntity
)

data class CartSummary(
  val items: List<CartItemWithProduct> = emptyList(),
  val itemCount: Int = 0,
  val subtotal: Double = 0.0,
  val savings: Double = 0.0,
  val deliveryFee: Double = 0.0,
  val platformFee: Double = 3.0,
  val total: Double = 0.0
)

class BazaarViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = BazaarRepository.getInstance(application)

  init {
    repository.initSeedData(viewModelScope)
  }

  val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allStores: StateFlow<List<StoreEntity>> = repository.allStores
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val communityRequests: StateFlow<List<CommunityRequestEntity>> = repository.communityRequests
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val marketRates: StateFlow<List<MarketRateEntity>> = repository.marketRates
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Navigation & User Auth state
  private val _isLoggedIn = MutableStateFlow(true)
  val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

  val availableAccounts = listOf(
    UserAccount(
      name = "Silgrak Marak",
      email = "silgrakmarak1309@gmail.com",
      role = "SUPER ADMIN",
      isAdmin = true,
      avatarInitial = "S",
      avatarColorHex = 0xFF0284C7
    ),
    UserAccount(
      name = "Greja Marak",
      email = "grejamarak@gmail.com",
      role = "BUYER",
      isAdmin = false,
      avatarInitial = "G",
      avatarColorHex = 0xFF16A34A
    ),
    UserAccount(
      name = "Demo Local Seller",
      email = "seller@localbazaar.com",
      role = "SELLER",
      isAdmin = false,
      avatarInitial = "D",
      avatarColorHex = 0xFF9333EA
    )
  )

  private val _userRecords = MutableStateFlow(
    listOf(
      UserRecord("USR-101", "Silgrak Marak", "silgrakmarak1309@gmail.com", "+91 98765 00001", "SUPER ADMIN", isBlocked = false, joinedDate = "Jan 2026", avatarEmoji = "👑"),
      UserRecord("USR-102", "Greja Marak", "grejamarak@gmail.com", "+91 98765 00002", "BUYER", isBlocked = false, joinedDate = "Feb 2026", avatarEmoji = "👩"),
      UserRecord("USR-103", "Kisan Fresh Organics", "seller.kisan@localbazaar.com", "+91 98765 11001", "SELLER", isBlocked = false, joinedDate = "Mar 2026", avatarEmoji = "🥦"),
      UserRecord("USR-104", "Sharma Electricals", "sharma.cooling@localbazaar.com", "+91 98765 22002", "SELLER", isBlocked = false, joinedDate = "Apr 2026", avatarEmoji = "⚡"),
      UserRecord("USR-105", "Rohit Sharma", "rohit.tech@localbazaar.com", "+91 98765 33003", "SELLER", isBlocked = false, joinedDate = "May 2026", avatarEmoji = "📱"),
      UserRecord("USR-106", "Sengbath Sangma", "sengbath.s@gmail.com", "+91 98765 44004", "BUYER", isBlocked = false, joinedDate = "Jun 2026", avatarEmoji = "👨")
    )
  )
  val userRecords: StateFlow<List<UserRecord>> = _userRecords.asStateFlow()

  fun toggleBlockUser(userId: String) {
    _userRecords.value = _userRecords.value.map { user ->
      if (user.id == userId) {
        user.copy(isBlocked = !user.isBlocked)
      } else {
        user
      }
    }
  }

  private val _currentUser = MutableStateFlow(availableAccounts[0])
  val currentUser: StateFlow<UserAccount> = _currentUser.asStateFlow()

  private val _currentTab = MutableStateFlow(AppNavTab.HOME)
  val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

  private val _authTab = MutableStateFlow("EMAIL_GOOGLE") // "EMAIL_GOOGLE", "PHONE_OTP"
  val authTab: StateFlow<String> = _authTab.asStateFlow()

  private val _isAccountChooserOpen = MutableStateFlow(false)
  val isAccountChooserOpen: StateFlow<Boolean> = _isAccountChooserOpen.asStateFlow()

  // Location State
  private val _selectedState = MutableStateFlow("Meghalaya")
  val selectedState: StateFlow<String> = _selectedState.asStateFlow()

  private val _selectedDistrict = MutableStateFlow("West Garo Hills (Tura)")
  val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

  private val _isLocationDialogOpen = MutableStateFlow(false)
  val isLocationDialogOpen: StateFlow<Boolean> = _isLocationDialogOpen.asStateFlow()

  // Search & Filter state
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCategory = MutableStateFlow("All")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _filterType = MutableStateFlow("ALL") // "ALL", "VERIFIED", "SERVICES", "PRODUCTS"
  val filterType: StateFlow<String> = _filterType.asStateFlow()

  private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
  val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

  // Modals & Dialogs
  private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
  val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

  private val _isCartOpen = MutableStateFlow(false)
  val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

  private val _selectedOrderForTracking = MutableStateFlow<OrderEntity?>(null)
  val selectedOrderForTracking: StateFlow<OrderEntity?> = _selectedOrderForTracking.asStateFlow()

  private val _isPostAdDialogOpen = MutableStateFlow(false)
  val isPostAdDialogOpen: StateFlow<Boolean> = _isPostAdDialogOpen.asStateFlow()

  private val _isAdminPanelOpen = MutableStateFlow(false)
  val isAdminPanelOpen: StateFlow<Boolean> = _isAdminPanelOpen.asStateFlow()

  private val _isPostRequestDialogOpen = MutableStateFlow(false)
  val isPostRequestDialogOpen: StateFlow<Boolean> = _isPostRequestDialogOpen.asStateFlow()

  private val _orderSuccessMessage = MutableStateFlow<String?>(null)
  val orderSuccessMessage: StateFlow<String?> = _orderSuccessMessage.asStateFlow()

  // Chats Data & Conversation
  private val _chatThreads = MutableStateFlow(
    listOf(
      ChatThread(
        id = "chat-1",
        contactName = "Kisan Fresh Organics",
        contactSubtitle = "Tura Bazaar, West Garo Hills",
        lastMessage = "Is your vegetable basket available today?",
        time = "10:15 AM",
        unreadCount = 1,
        avatarEmoji = "🥦",
        messages = listOf(
          ChatMessage("m1", "You", "Hello, is the fresh organic basket available for today?", "10:12 AM", true),
          ChatMessage("m2", "Kisan Fresh Organics", "Yes! Freshly harvested organic vegetables and fruits basket is available. Can deliver within 25 minutes.", "10:15 AM", false)
        )
      ),
      ChatThread(
        id = "chat-2",
        contactName = "Sharma Electricals & Cooling",
        contactSubtitle = "Araimile, West Garo Hills",
        lastMessage = "Technician will arrive at your address at 3:30 PM",
        time = "Yesterday",
        unreadCount = 0,
        avatarEmoji = "⚡",
        messages = listOf(
          ChatMessage("m3", "You", "Hi, I booked an AC repair inspection.", "Yesterday 2:00 PM", true),
          ChatMessage("m4", "Sharma Electricals & Cooling", "Technician will arrive at your address at 3:30 PM with tools and replacement parts.", "Yesterday 2:15 PM", false)
        )
      ),
      ChatThread(
        id = "chat-3",
        contactName = "Rohit Sharma",
        contactSubtitle = "Hawakhana, West Garo Hills",
        lastMessage = "Yes, original bill and charger are included with the OnePlus.",
        time = "Yesterday",
        unreadCount = 0,
        avatarEmoji = "📱",
        messages = listOf(
          ChatMessage("m5", "You", "Is the phone bill available?", "Yesterday 11:30 AM", true),
          ChatMessage("m6", "Rohit Sharma", "Yes, original bill and charger are included with the OnePlus.", "Yesterday 11:45 AM", false)
        )
      )
    )
  )
  val chatThreads: StateFlow<List<ChatThread>> = _chatThreads.asStateFlow()

  private val _activeChatThread = MutableStateFlow<ChatThread?>(null)
  val activeChatThread: StateFlow<ChatThread?> = _activeChatThread.asStateFlow()

  // Computed Cart Summary
  val cartSummary: StateFlow<CartSummary> = combine(
    repository.cartItems,
    repository.allProducts
  ) { cartItems, products ->
    val productMap = products.associateBy { it.id }
    val itemsWithProducts = cartItems.mapNotNull { cartItem ->
      productMap[cartItem.productId]?.let { product ->
        CartItemWithProduct(cartItem, product)
      }
    }

    val totalCount = itemsWithProducts.sumOf { it.cartItem.quantity }
    val subtotal = itemsWithProducts.sumOf { it.product.price * it.cartItem.quantity }
    val originalSubtotal = itemsWithProducts.sumOf { it.product.originalPrice * it.cartItem.quantity }
    val savings = (originalSubtotal - subtotal).coerceAtLeast(0.0)
    val deliveryFee = if (subtotal == 0.0 || subtotal >= 199.0) 0.0 else 19.0
    val platformFee = if (subtotal > 0) 3.0 else 0.0
    val total = if (subtotal > 0) subtotal + deliveryFee + platformFee else 0.0

    CartSummary(
      items = itemsWithProducts,
      itemCount = totalCount,
      subtotal = subtotal,
      savings = savings,
      deliveryFee = deliveryFee,
      platformFee = platformFee,
      total = total
    )
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary())

  // Navigation & Screen Controls
  fun setTab(tab: AppNavTab) {
    _currentTab.value = tab
  }

  fun setAuthTab(tab: String) {
    _authTab.value = tab
  }

  fun setLoggedIn(loggedIn: Boolean) {
    _isLoggedIn.value = loggedIn
  }

  fun selectUserAccount(account: UserAccount) {
    _currentUser.value = account
    _isAccountChooserOpen.value = false
    _isLoggedIn.value = true
  }

  fun setAccountChooserOpen(open: Boolean) {
    _isAccountChooserOpen.value = open
  }

  fun setLocation(state: String, district: String) {
    _selectedState.value = state
    _selectedDistrict.value = district
    _isLocationDialogOpen.value = false
  }

  fun setLocationDialogOpen(open: Boolean) {
    _isLocationDialogOpen.value = open
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setFilterType(filter: String) {
    _filterType.value = if (_filterType.value == filter) "ALL" else filter
  }

  fun toggleFavorite(productId: Int) {
    val current = _favorites.value.toMutableSet()
    if (current.contains(productId)) {
      current.remove(productId)
    } else {
      current.add(productId)
    }
    _favorites.value = current
  }

  fun selectProduct(product: ProductEntity?) {
    _selectedProduct.value = product
  }

  fun setCartOpen(open: Boolean) {
    _isCartOpen.value = open
  }

  fun selectOrderForTracking(order: OrderEntity?) {
    _selectedOrderForTracking.value = order
  }

  fun setPostAdDialogOpen(open: Boolean) {
    _isPostAdDialogOpen.value = open
  }

  fun setAdminPanelOpen(open: Boolean) {
    _isAdminPanelOpen.value = open
  }

  fun setPostRequestDialogOpen(open: Boolean) {
    _isPostRequestDialogOpen.value = open
  }

  fun clearOrderSuccessMessage() {
    _orderSuccessMessage.value = null
  }

  // Chats
  fun openChat(thread: ChatThread) {
    _activeChatThread.value = thread
  }

  fun openChatForVendor(vendorName: String, locality: String) {
    val existing = _chatThreads.value.find { it.contactName.equals(vendorName, ignoreCase = true) }
    if (existing != null) {
      _activeChatThread.value = existing
    } else {
      val newThread = ChatThread(
        id = "chat-${System.currentTimeMillis()}",
        contactName = vendorName,
        contactSubtitle = locality,
        lastMessage = "Inquiry regarding listing",
        time = "Just now",
        unreadCount = 0,
        avatarEmoji = "💬",
        messages = listOf(
          ChatMessage("msg-${System.currentTimeMillis()}", "You", "Hi, I am interested in your listing on Local Bazaar.", "Just now", true)
        )
      )
      _chatThreads.value = listOf(newThread) + _chatThreads.value
      _activeChatThread.value = newThread
    }
  }

  fun closeActiveChat() {
    _activeChatThread.value = null
  }

  fun sendChatMessage(text: String) {
    val current = _activeChatThread.value ?: return
    if (text.isBlank()) return

    val newMsg = ChatMessage(
      id = "msg-${System.currentTimeMillis()}",
      senderName = "You",
      text = text,
      time = "Just now",
      isMe = true
    )
    val updatedThread = current.copy(
      messages = current.messages + newMsg,
      lastMessage = text,
      time = "Just now"
    )
    _activeChatThread.value = updatedThread
    _chatThreads.value = _chatThreads.value.map {
      if (it.id == current.id) updatedThread else it
    }
  }

  // Cart operations
  fun addToCart(productId: Int) {
    viewModelScope.launch {
      val existing = cartSummary.value.items.find { it.cartItem.productId == productId }
      if (existing != null) {
        repository.updateCartQuantity(productId, existing.cartItem.quantity + 1)
      } else {
        repository.addToCart(productId)
      }
    }
  }

  fun updateCartQuantity(productId: Int, quantity: Int) {
    viewModelScope.launch {
      repository.updateCartQuantity(productId, quantity)
    }
  }

  fun removeFromCart(productId: Int) {
    viewModelScope.launch {
      repository.removeFromCart(productId)
    }
  }

  fun clearCart() {
    viewModelScope.launch {
      repository.clearCart()
    }
  }

  fun placeOrder(
    deliveryAddress: String,
    deliveryInstructions: String,
    paymentMethod: String
  ) {
    viewModelScope.launch {
      val summary = cartSummary.value
      if (summary.items.isEmpty()) return@launch

      val itemsDesc = summary.items.joinToString(", ") {
        "${it.cartItem.quantity}x ${it.product.name}"
      }
      val primaryVendor = summary.items.first().product.vendorName

      val orderId = repository.placeOrder(
        itemsSummary = itemsDesc,
        totalAmount = summary.total,
        itemCount = summary.itemCount,
        deliveryAddress = deliveryAddress.ifBlank { "House 24, Hawakhana, Tura, West Garo Hills" },
        deliveryInstructions = deliveryInstructions.ifBlank { "Leave at doorstep" },
        paymentMethod = paymentMethod,
        vendorName = primaryVendor
      )

      _isCartOpen.value = false
      _orderSuccessMessage.value = "Order #$orderId placed successfully with $primaryVendor!"
    }
  }

  fun updateOrderStatus(orderId: String, status: String) {
    viewModelScope.launch {
      repository.updateOrderStatus(orderId, status)
      if (_selectedOrderForTracking.value?.orderId == orderId) {
        _selectedOrderForTracking.value = _selectedOrderForTracking.value?.copy(orderStatus = status)
      }
    }
  }

  // Post Ad
  fun postNewListing(
    title: String,
    category: String,
    price: Double,
    description: String,
    isService: Boolean,
    phoneOrWhatsapp: String = "",
    locality: String = "Tura Bazaar, West Garo Hills"
  ) {
    viewModelScope.launch {
      val cleanPhone = phoneOrWhatsapp.trim().ifBlank { "+91 98765 11001" }
      val newProduct = ProductEntity(
        name = title,
        category = category,
        price = price,
        originalPrice = price * 1.2,
        unit = if (isService) "Service Visit" else "1 Unit",
        vendorName = _currentUser.value.name,
        vendorLocality = locality,
        vendorPhone = cleanPhone,
        distanceKm = 0.5,
        rating = 5.0,
        reviewCount = 1,
        description = description,
        inStock = true,
        isFarmerDirect = category.contains("Grocery", ignoreCase = true),
        badge = if (isService) "SERVICE" else "ITEM",
        harvestOrPackDate = "Listed Today",
        isCustomAdded = true,
        iconEmoji = if (isService) "🔧" else if (category.contains("Mobile")) "📱" else "🛍️"
      )
      repository.addNewProduct(newProduct)
      _isPostAdDialogOpen.value = false
    }
  }

  fun updateProductStock(productId: Int, inStock: Boolean) {
    viewModelScope.launch {
      repository.updateProductStock(productId, inStock)
    }
  }

  fun deleteProduct(productId: Int) {
    viewModelScope.launch {
      repository.deleteProduct(productId)
    }
  }

  fun postCommunityRequest(
    title: String,
    description: String,
    category: String,
    requesterName: String,
    locality: String
  ) {
    viewModelScope.launch {
      repository.postCommunityRequest(
        title = title,
        description = description,
        category = category,
        requesterName = requesterName.ifBlank { _currentUser.value.name },
        locality = locality.ifBlank { "Tura Bazaar, West Garo Hills" }
      )
      _isPostRequestDialogOpen.value = false
    }
  }

  fun offerFulfillment(requestId: Int) {
    viewModelScope.launch {
      repository.offerFulfillment(requestId)
    }
  }
}
