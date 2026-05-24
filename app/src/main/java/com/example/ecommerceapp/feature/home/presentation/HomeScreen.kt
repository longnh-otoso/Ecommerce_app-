package com.example.ecommerceapp.feature.home.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.model.Category
import com.example.ecommerceapp.screens.home.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            MyTopAppBar(
                onCartClick = onCartClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val searchQuery = remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

            SearchBar(
                query = searchQuery.value,
                onQueryChange = { searchQuery.value = it },
                onSearch = { focusManager.clearFocus() },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            // Categories Header
            SectionTitle("Categories", "See All") {}

            val categories = listOf(
                Category(1, "Electronics", "https://cdn-icons-png.flaticon.com/128/716/716429.png"),
                Category(2, "Clothing", "https://cdn-icons-png.flaticon.com/128/2503/2503380.png"),
                Category(3, "Home", "https://cdn-icons-png.flaticon.com/128/619/619153.png"),
                Category(4, "Shoes", "https://cdn-icons-png.flaticon.com/128/2741/2741298.png"),
                Category(5, "Beauty", "https://cdn-icons-png.flaticon.com/128/3163/3163200.png")
            )
            val selectedCategory = remember { mutableStateOf(categories[0]) }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        icon = category.iconUrL,
                        text = category.name,
                        isselected = category == selectedCategory.value,
                        onClick = { selectedCategory.value = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Products Header
            SectionTitle("Featured Products", "See All") {}

            // Handle UI State
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is HomeUiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is HomeUiState.Success -> {
                    val filteredProducts = state.products.filter {
                        it.name.contains(searchQuery.value, ignoreCase = true)
                    }

                    if (filteredProducts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Không tìm thấy sản phẩm nào")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.weight(1f)
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
