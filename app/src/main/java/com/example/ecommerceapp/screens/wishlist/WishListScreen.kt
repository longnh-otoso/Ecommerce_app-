package com.example.ecommerceapp.screens.wishlist

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecommerceapp.feature.home.presentation.HomeUiState
import com.example.ecommerceapp.feature.home.presentation.HomeViewModel
import com.example.ecommerceapp.screens.home.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishListScreen(
    viewModel: HomeViewModel,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE) }
    
    // Wishlist state loaded from SharedPreferences
    var wishlistIds by remember {
        mutableStateOf(sharedPreferences.getStringSet("wishlist_items", emptySet()) ?: emptySet())
    }

    val uiState by viewModel.uiState.collectAsState()

    // Key event listener to reload wishlist from Shared Prefs
    LaunchedEffect(Unit) {
        wishlistIds = sharedPreferences.getStringSet("wishlist_items", emptySet()) ?: emptySet()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Danh sách yêu thích", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7))
        ) {
            if (wishlistIds.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Danh sách yêu thích của bạn trống",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                when (val state = uiState) {
                    is HomeUiState.Loading -> {
                        CircularProgressIndicator(
                            color = Color(0xFF1565C0),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is HomeUiState.Error -> {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    is HomeUiState.Success -> {
                        val filteredProducts = remember(state.products, wishlistIds) {
                            state.products.filter { it.id in wishlistIds }
                        }

                        if (filteredProducts.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(80.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Danh sách yêu thích của bạn trống",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredProducts) { product ->
                                    ProductCard(
                                        product = product,
                                        onClick = { onProductClick(product.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
