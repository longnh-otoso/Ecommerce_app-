package com.example.ecommerceapp.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.model.Category
import com.example.ecommerceapp.model.Product

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSeeAllCategoriesClick: () -> Unit // Thêm callback này
){
    Scaffold(
        topBar = {
            MyTopAppBar(
                onCartClick = onCartClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            val searchQuery = remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current
            SearchBar(
                query = searchQuery.value,
                onQueryChange = {
                    searchQuery.value = it
                },
                onSearch = {
                    focusManager.clearFocus()
                },
                modifier = Modifier.padding(16.dp).fillMaxWidth()

            )

            SectionTitle("Categories", "See All") { 
                onSeeAllCategoriesClick() // Gọi khi nhấn See All
            }

            // Mock the categories list
            val categories: List<Category> = listOf(
                Category(1, "Electronics", "https://cdn-icons-png.flaticon.com/128/716/716429.png"),
                Category(2, "Clothing", "https://cdn-icons-png.flaticon.com/128/2503/2503380.png"),
                Category(3, "Home", "https://cdn-icons-png.flaticon.com/128/619/619153.png"),
                Category(4, "Shoes", "https://cdn-icons-png.flaticon.com/128/2741/2741298.png"),
                Category(5, "Beauty", "https://cdn-icons-png.flaticon.com/128/3163/3163200.png"),
            )

            //the selected category

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
                        onClick = {
                            selectedCategory.value = category
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Featured", "See All") {
                // Xử lý khi nhấn See All
            }

            val productList = listOf(
                Product(
                    id = "1",
                    name = "Smartphone X",
                    description = "Màn hình OLED 6.7 inch, Chip A15 Bionic",
                    price = 999.0,
                    imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=300&auto=format&fit=crop",
                    rating = 4.8,
                    discount = 15
                ),
                Product(
                    id = "2",
                    name = "MacBook Pro",
                    description = "Chip M2, RAM 16GB, SSD 512GB",
                    price = 1499.0,
                    imageUrl = "https://cellphones.com.vn/sforum/wp-content/uploads/2021/05/render-MacBook-Pro-2021-1.jpg",
                    rating = 4.9,
                    discount = 10
                ),
                Product(
                    id = "3",
                    name = "Sony Headphones",
                    description = "Chống ồn chủ động, Pin 30h",
                    price = 349.0,
                    imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=300&auto=format&fit=crop",
                    rating = 4.7,
                    discount = 20
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(productList) { product ->
                    ProductCard(product = product, onClick = { /* Mở chi tiết sản phẩm */ })
                }
            }
        }
    }
}

