package com.example.ecommerceapp.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.feature.home.presentation.HomeUiState
import com.example.ecommerceapp.feature.home.presentation.HomeViewModel
import com.example.ecommerceapp.screens.home.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    categoryName: String,
    viewModel: HomeViewModel,
    onProductClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(categoryName, fontWeight = FontWeight.Bold) },
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
                    val filteredProducts = remember(state.products, categoryName) {
                        state.products.filter { product ->
                            when (categoryName) {
                                "Tất cả" -> true
                                "Electronics" -> product.category in listOf("smartphones", "laptops", "tablets", "electronics", "mobile-accessories")
                                "Clothing" -> product.category in listOf("clothing", "womens-dresses", "womens-clothing", "mens-shirts", "mens-clothing", "tops")
                                "Home" -> product.category in listOf("home-decoration", "furniture", "home", "kitchen-accessories")
                                "Shoes" -> product.category in listOf("shoes", "mens-shoes", "womens-shoes")
                                "Beauty" -> product.category in listOf("beauty", "fragrances", "skin-care")
                                else -> product.category.contains(categoryName, ignoreCase = true)
                            }
                        }
                    }

                    if (filteredProducts.isEmpty()) {
                        Text(
                            text = "Không tìm thấy sản phẩm nào trong danh mục này",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
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
