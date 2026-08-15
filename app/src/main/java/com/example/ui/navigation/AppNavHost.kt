package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.model.ListingItem
import com.example.ui.screens.admin.AdminModerationScreen
import com.example.ui.screens.auth.AuthLoginScreen
import com.example.ui.screens.chat.ChatConversationScreen
import com.example.ui.screens.chat.MessagesScreen
import com.example.ui.screens.details.ListingDetailScreen
import com.example.ui.screens.explore.ExploreScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.onboarding.OnboardingLocationScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.safety.SafetyCenterScreen
import com.example.ui.screens.sell.SellScreen
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun LocalBazaarApp(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val unreadMessagesCount by viewModel.unreadMessagesCount.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Explore.route,
        Screen.Messages.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    unreadMessagesCount = unreadMessagesCount
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToListing = { id -> navController.navigate(NavDestinations.listingDetail(id)) },
                    onNavigateToExplore = { cat, isSrv ->
                        viewModel.setCategoryFilter(cat, isSrv)
                        navController.navigate(Screen.Explore.route)
                    },
                    onNavigateToSell = { navController.navigate(Screen.Sell.route) },
                    onNavigateToNotifications = { navController.navigate(NavDestinations.NOTIFICATIONS) },
                    onNavigateToSafety = { navController.navigate(NavDestinations.SAFETY_CENTER) },
                    onNavigateToAdminModeration = { navController.navigate(NavDestinations.ADMIN_MODERATION) },
                    onOpenChat = { listing ->
                        viewModel.getOrCreateConversation(listing) { convId ->
                            navController.navigate(NavDestinations.chatConversation(convId))
                        }
                    }
                )
            }

            composable(Screen.Explore.route) {
                ExploreScreen(
                    viewModel = viewModel,
                    onNavigateToListing = { id -> navController.navigate(NavDestinations.listingDetail(id)) },
                    onOpenChat = { listing ->
                        viewModel.getOrCreateConversation(listing) { convId ->
                            navController.navigate(NavDestinations.chatConversation(convId))
                        }
                    }
                )
            }

            composable(Screen.Sell.route) {
                SellScreen(
                    viewModel = viewModel,
                    onListingCreated = { newId ->
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(Screen.Messages.route) {
                MessagesScreen(
                    viewModel = viewModel,
                    onOpenConversation = { convId ->
                        navController.navigate(NavDestinations.chatConversation(convId))
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToListing = { id -> navController.navigate(NavDestinations.listingDetail(id)) },
                    onNavigateToSafety = { navController.navigate(NavDestinations.SAFETY_CENTER) },
                    onNavigateToAdminModeration = { navController.navigate(NavDestinations.ADMIN_MODERATION) },
                    onNavigateToNotifications = { navController.navigate(NavDestinations.NOTIFICATIONS) },
                    onNavigateToLogin = {
                        navController.navigate(NavDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = NavDestinations.LISTING_DETAIL,
                arguments = listOf(navArgument("listingId") { type = NavType.StringType })
            ) { backStack ->
                val listingId = backStack.arguments?.getString("listingId") ?: ""
                ListingDetailScreen(
                    listingId = listingId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenChat = { listing ->
                        viewModel.getOrCreateConversation(listing) { convId ->
                            navController.navigate(NavDestinations.chatConversation(convId))
                        }
                    },
                    onNavigateToListing = { nextId ->
                        navController.navigate(NavDestinations.listingDetail(nextId))
                    },
                    onNavigateToSafety = {
                        navController.navigate(NavDestinations.SAFETY_CENTER)
                    }
                )
            }

            composable(
                route = NavDestinations.CHAT_CONVERSATION,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { backStack ->
                val conversationId = backStack.arguments?.getString("conversationId") ?: ""
                ChatConversationScreen(
                    conversationId = conversationId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToListing = { listingId ->
                        navController.navigate(NavDestinations.listingDetail(listingId))
                    },
                    onNavigateToSafety = {
                        navController.navigate(NavDestinations.SAFETY_CENTER)
                    }
                )
            }

            composable(NavDestinations.SAFETY_CENTER) {
                SafetyCenterScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavDestinations.ADMIN_MODERATION) {
                AdminModerationScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavDestinations.NOTIFICATIONS) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenListing = { id -> navController.navigate(NavDestinations.listingDetail(id)) },
                    onOpenChat = { navController.navigate(Screen.Messages.route) }
                )
            }

            composable(NavDestinations.LOGIN) {
                AuthLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(NavDestinations.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(NavDestinations.ONBOARDING) {
                OnboardingLocationScreen(
                    viewModel = viewModel,
                    onComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(NavDestinations.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
