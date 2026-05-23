package com.example.ecommerceapp.screens.categories

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecommerceapp.model.Category
// Xóa import cũ bị lỗi và không cần thiết

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (Category) -> Unit
) {
    val categories = listOf(
        Category(1, "Electronics", "https://images.unsplash.com/photo-1498049794561-7780e7231661?q=80&w=400"),
        Category(2, "Clothing", "https://images.unsplash.com/photo-1489987707025-afc232f7ea0f?q=80&w=400"),
        Category(3, "Home", "https://images.unsplash.com/photo-1484101403633-562f6cff80bd?q=80&w=400"),
        Category(4, "Shoes", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=400"),
        Category(5, "Beauty", "https://images.unsplash.com/photo-1522335789203-abd1fc54bc9?q=80&w=400"),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tất cả danh mục", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category = category) {
                    onCategoryClick(category)
                }
            }
        }
    }
}
