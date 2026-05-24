package com.example.ecommerceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ecommerceapp.feature.auth.presentation.AuthViewModel
import com.example.ecommerceapp.feature.auth.presentation.LoginScreen
import com.example.ecommerceapp.feature.auth.presentation.RegisterScreen
import com.example.ecommerceapp.feature.cart.presentation.CartScreen
import com.example.ecommerceapp.feature.cart.presentation.CartViewModel
import com.example.ecommerceapp.feature.goods.presentation.ProductDetailScreen
import com.example.ecommerceapp.feature.goods.presentation.ProductDetailViewModel
import com.example.ecommerceapp.feature.home.presentation.HomeScreen
import com.example.ecommerceapp.feature.home.presentation.HomeViewModel
import com.example.ecommerceapp.screens.home.BottomNavigationBar
import com.example.ecommerceapp.feature.order.presentation.OrderScreen
import com.example.ecommerceapp.feature.order.presentation.OrderViewModel
import com.example.ecommerceapp.feature.profile.presentation.ProfileScreen
import com.example.ecommerceapp.feature.profile.presentation.ProfileViewModel
import com.example.ecommerceapp.ui.theme.EcommerceAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcommerceAppTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val currentUser by authViewModel.currentUser.collectAsState()

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val mainTabs = listOf("Home", "Categories", "WishList", "Cart", "Profile")
                val showBottomBar = currentRoute in mainTabs

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    if (route == "Profile" && currentUser == null) {
                                        navController.navigate("Login") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    } else {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val detailViewModel: ProductDetailViewModel = hiltViewModel()
                    val cartViewModel: CartViewModel = hiltViewModel()
                    val orderViewModel: OrderViewModel = hiltViewModel()
                    val profileViewModel: ProfileViewModel = hiltViewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "Home",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("Home") {
                            HomeScreen(
                                viewModel = homeViewModel,
                                onProductClick = { productId ->
                                    navController.navigate("ProductDetails/$productId")
                                },
                                onCartClick = {
                                    navController.navigate("Cart")
                                },
                                onProfileClick = {
                                    if (currentUser == null) {
                                        navController.navigate("Login")
                                    } else {
                                        navController.navigate("Profile")
                                    }
                                }
                            )
                        }
                        composable("Login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onNavigateToRegister = {
                                    navController.navigate("Register")
                                },
                                onLoginSuccess = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate("Home")
                                    }
                                },
                                onBackClick = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate("Home")
                                    }
                                }
                            )
                        }
                        composable("Register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate("Home")
                                    }
                                }
                            )
                        }
                        composable("Categories") {
                            PlaceholderScreen(title = "Categories")
                        }
                        composable("WishList") {
                            PlaceholderScreen(title = "WishList")
                        }
                        composable("Cart") {
                            CartScreen(
                                viewModel = cartViewModel,
                                onBackClick = {
                                    if (!navController.popBackStack()) {
                                        navController.navigate("Home")
                                    }
                                },
                                onCheckoutClick = {
                                    if (currentUser == null) {
                                        navController.navigate("Login")
                                    } else {
                                        val items = cartViewModel.cartItems.value
                                        val total = cartViewModel.totalAmount.value
                                        orderViewModel.checkout(currentUser?.uid ?: "", items, total)
                                        navController.navigate("Order")
                                    }
                                }
                            )
                        }
                        composable(
                            route = "ProductDetails/{productId}",
                            arguments = listOf(navArgument("productId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val productId = backStackEntry.arguments?.getString("productId") ?: ""
                            ProductDetailScreen(
                                productId = productId,
                                viewModel = detailViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("Order") {
                            OrderScreen(
                                userId = currentUser?.uid ?: "",
                                viewModel = orderViewModel,
                                onBackClick = {
                                    navController.navigate("Home") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                    }
                                }
                            )
                        }
                        composable("Profile") {
                            ProfileScreen(
                                viewModel = profileViewModel,
                                onOrdersClick = {
                                    navController.navigate("Order")
                                },
                                onSignOutSuccess = {
                                    authViewModel.signOut()
                                    navController.navigate("Home") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565c0)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tính năng này đang được phát triển",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
