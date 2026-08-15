package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppNavTab
import com.example.ui.BazaarViewModel
import com.example.ui.dialogs.AdminControlDialog
import com.example.ui.dialogs.CartBottomSheet
import com.example.ui.dialogs.GoogleAccountChooserSheet
import com.example.ui.dialogs.PostAdDialog
import com.example.ui.dialogs.ProductDetailDialog
import com.example.ui.dialogs.SelectLocationDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.LocalBazaarTheme

class MainActivity : ComponentActivity() {
  private val viewModel: BazaarViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      LocalBazaarTheme {
        LocalBazaarApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun LocalBazaarApp(viewModel: BazaarViewModel) {
  val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
  val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val authTab by viewModel.authTab.collectAsStateWithLifecycle()
  val isAccountChooserOpen by viewModel.isAccountChooserOpen.collectAsStateWithLifecycle()

  val products by viewModel.allProducts.collectAsStateWithLifecycle()
  val selectedState by viewModel.selectedState.collectAsStateWithLifecycle()
  val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
  val isLocationDialogOpen by viewModel.isLocationDialogOpen.collectAsStateWithLifecycle()

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val filterType by viewModel.filterType.collectAsStateWithLifecycle()
  val favorites by viewModel.favorites.collectAsStateWithLifecycle()

  val chatThreads by viewModel.chatThreads.collectAsStateWithLifecycle()
  val activeChatThread by viewModel.activeChatThread.collectAsStateWithLifecycle()

  val cartSummary by viewModel.cartSummary.collectAsStateWithLifecycle()
  val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
  val isCartOpen by viewModel.isCartOpen.collectAsStateWithLifecycle()
  val isPostAdDialogOpen by viewModel.isPostAdDialogOpen.collectAsStateWithLifecycle()
  val isAdminPanelOpen by viewModel.isAdminPanelOpen.collectAsStateWithLifecycle()
  val userRecords by viewModel.userRecords.collectAsStateWithLifecycle()
  val orderSuccessMessage by viewModel.orderSuccessMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(orderSuccessMessage) {
    orderSuccessMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearOrderSuccessMessage()
    }
  }

  if (!isLoggedIn) {
    // Auth / Sign In View
    AuthScreen(
      authTab = authTab,
      onAuthTabChange = { viewModel.setAuthTab(it) },
      onGoogleSignInClick = { viewModel.setAccountChooserOpen(true) },
      onEmailSignIn = { email ->
        val matching = viewModel.availableAccounts.find { it.email.equals(email, ignoreCase = true) }
        if (matching != null) {
          viewModel.selectUserAccount(matching)
        } else {
          viewModel.setLoggedIn(true)
        }
      }
    )

    if (isAccountChooserOpen) {
      GoogleAccountChooserSheet(
        accounts = viewModel.availableAccounts,
        onAccountSelected = { account ->
          viewModel.selectUserAccount(account)
        },
        onDismiss = { viewModel.setAccountChooserOpen(false) }
      )
    }
    return
  }

  // Main Marketplace App View
  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("main_scaffold"),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      Column {
        // Floating Cart Bar (appears when items are in cart)
        AnimatedVisibility(
          visible = cartSummary.itemCount > 0,
          enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
          exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 6.dp)
              .clip(RoundedCornerShape(14.dp))
              .clickable { viewModel.setCartOpen(true) }
              .testTag("floating_cart_bar"),
            color = Color(0xFF15803D),
            shadowElevation = 6.dp
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "${cartSummary.itemCount}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "₹${cartSummary.total.toInt()}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = Color.White
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "View Basket",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.ArrowForward,
                  contentDescription = "View Basket",
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }

        // Bottom Navigation Bar with 5 slots: Home, Categories, [+] FAB, Chats, Profile
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = Color.White,
          shadowElevation = 8.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .navigationBarsPadding()
              .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Home Tab
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { viewModel.setTab(AppNavTab.HOME) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag("nav_home")
            ) {
              Icon(
                imageVector = if (currentTab == AppNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = "Home",
                tint = if (currentTab == AppNavTab.HOME) Color(0xFF15803D) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "Home",
                fontSize = 10.sp,
                fontWeight = if (currentTab == AppNavTab.HOME) FontWeight.Bold else FontWeight.Medium,
                color = if (currentTab == AppNavTab.HOME) Color(0xFF15803D) else Color(0xFF94A3B8)
              )
            }

            // Categories Tab
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { viewModel.setTab(AppNavTab.CATEGORIES) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag("nav_categories")
            ) {
              Icon(
                imageVector = if (currentTab == AppNavTab.CATEGORIES) Icons.Filled.GridView else Icons.Outlined.GridView,
                contentDescription = "Categories",
                tint = if (currentTab == AppNavTab.CATEGORIES) Color(0xFF15803D) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "Categories",
                fontSize = 10.sp,
                fontWeight = if (currentTab == AppNavTab.CATEGORIES) FontWeight.Bold else FontWeight.Medium,
                color = if (currentTab == AppNavTab.CATEGORIES) Color(0xFF15803D) else Color(0xFF94A3B8)
              )
            }

            // Center + Post Ad Button
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF15803D))
                .clickable { viewModel.setPostAdDialogOpen(true) }
                .testTag("nav_center_post_ad"),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Post Ad",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
              )
            }

            // Chats Tab
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { viewModel.setTab(AppNavTab.CHATS) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag("nav_chats")
            ) {
              Icon(
                imageVector = if (currentTab == AppNavTab.CHATS) Icons.Filled.Chat else Icons.Outlined.Chat,
                contentDescription = "Chats",
                tint = if (currentTab == AppNavTab.CHATS) Color(0xFF15803D) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "Chats",
                fontSize = 10.sp,
                fontWeight = if (currentTab == AppNavTab.CHATS) FontWeight.Bold else FontWeight.Medium,
                color = if (currentTab == AppNavTab.CHATS) Color(0xFF15803D) else Color(0xFF94A3B8)
              )
            }

            // Profile Tab
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { viewModel.setTab(AppNavTab.PROFILE) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .testTag("nav_profile")
            ) {
              Icon(
                imageVector = if (currentTab == AppNavTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = if (currentTab == AppNavTab.PROFILE) Color(0xFF15803D) else Color(0xFF94A3B8),
                modifier = Modifier.size(24.dp)
              )
              Text(
                text = "Profile",
                fontSize = 10.sp,
                fontWeight = if (currentTab == AppNavTab.PROFILE) FontWeight.Bold else FontWeight.Medium,
                color = if (currentTab == AppNavTab.PROFILE) Color(0xFF15803D) else Color(0xFF94A3B8)
              )
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppNavTab.HOME -> {
          HomeScreen(
            products = products,
            selectedState = selectedState,
            selectedDistrict = selectedDistrict,
            onOpenLocationSelector = { viewModel.setLocationDialogOpen(true) },
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.setSearchQuery(it) },
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.setSelectedCategory(it) },
            filterType = filterType,
            onFilterTypeSelected = { viewModel.setFilterType(it) },
            favorites = favorites,
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onProductClick = { viewModel.selectProduct(it) }
          )
        }
        AppNavTab.CATEGORIES -> {
          CategoriesScreen(
            onSelectCategory = { cat ->
              viewModel.setSelectedCategory(cat)
              viewModel.setTab(AppNavTab.HOME)
            }
          )
        }
        AppNavTab.CHATS -> {
          ChatsScreen(
            threads = chatThreads,
            activeThread = activeChatThread,
            onOpenThread = { viewModel.openChat(it) },
            onCloseThread = { viewModel.closeActiveChat() },
            onSendMessage = { viewModel.sendChatMessage(it) }
          )
        }
        AppNavTab.PROFILE -> {
          ProfileScreen(
            currentUser = currentUser,
            selectedState = selectedState,
            selectedDistrict = selectedDistrict,
            onOpenAdminPanel = { viewModel.setAdminPanelOpen(true) },
            onOpenLocationDialog = { viewModel.setLocationDialogOpen(true) },
            onOpenPostAdDialog = { viewModel.setPostAdDialogOpen(true) },
            onLogout = { viewModel.setLoggedIn(false) }
          )
        }
      }
    }
  }

  // Modals & Dialogs
  if (isLocationDialogOpen) {
    SelectLocationDialog(
      currentState = selectedState,
      currentDistrict = selectedDistrict,
      onDismiss = { viewModel.setLocationDialogOpen(false) },
      onSaveLocation = { state, district ->
        viewModel.setLocation(state, district)
      }
    )
  }

  if (isPostAdDialogOpen) {
    PostAdDialog(
      onDismiss = { viewModel.setPostAdDialogOpen(false) },
      onPostListing = { title, cat, price, desc, isService, phone ->
        viewModel.postNewListing(
          title = title,
          category = cat,
          price = price,
          description = desc,
          isService = isService,
          phoneOrWhatsapp = phone,
          locality = "$selectedDistrict, $selectedState"
        )
      }
    )
  }

  if (isAdminPanelOpen) {
    AdminControlDialog(
      userRecords = userRecords,
      onToggleBlockUser = { userId -> viewModel.toggleBlockUser(userId) },
      onDismiss = { viewModel.setAdminPanelOpen(false) }
    )
  }

  // Product Detail Dialog
  selectedProduct?.let { product ->
    val inCartQty = remember(cartSummary, product) {
      cartSummary.items.find { it.cartItem.productId == product.id }?.cartItem?.quantity ?: 0
    }
    ProductDetailDialog(
      product = product,
      cartQuantity = inCartQty,
      onAddToCart = { viewModel.addToCart(product.id) },
      onUpdateQuantity = { qty -> viewModel.updateCartQuantity(product.id, qty) },
      onChatWithSeller = { vendor, loc ->
        viewModel.openChatForVendor(vendor, loc)
        viewModel.setTab(AppNavTab.CHATS)
      },
      onDismiss = { viewModel.selectProduct(null) }
    )
  }

  // Cart / Checkout Bottom Sheet
  if (isCartOpen) {
    CartBottomSheet(
      cartSummary = cartSummary,
      onUpdateQuantity = { prodId, qty -> viewModel.updateCartQuantity(prodId, qty) },
      onRemoveItem = { viewModel.removeFromCart(it) },
      onClearCart = { viewModel.clearCart() },
      onPlaceOrder = { addr, note, payMethod ->
        viewModel.placeOrder(addr, note, payMethod)
      },
      onDismiss = { viewModel.setCartOpen(false) }
    )
  }
}
