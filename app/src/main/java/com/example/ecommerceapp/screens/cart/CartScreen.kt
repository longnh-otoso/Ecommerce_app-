package com.example.ecommerceapp.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.ecommerceapp.model.CartItem
import com.example.ecommerceapp.model.Product
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onBackClick: () -> Unit = {}
) {
    // Dữ liệu mẫu (Sử dụng mutableStateListOf để UI cập nhật tự động khi thêm/xóa/sửa)
    val cartItems = remember {
        mutableStateListOf(
            CartItem(
                Product(
                    id = "1",
                    name = "Smartphone X",
                    price = 999.0,
                    imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=300&auto=format&fit=crop",
                    discount = 15
                ),
                quantity = 1
            ),
            CartItem(
                Product(
                    id = "2",
                    name = "MacBook Pro",
                    price = 1499.0,
                    imageUrl = "https://images.unsplash.com/photo-1517336712461-48118dd56153?q=80&w=300&auto=format&fit=crop",
                    discount = 10
                ),
                quantity = 1
            )
        )
    }

    val totalAmount = cartItems.sumOf { it.product.price * it.quantity }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Giỏ hàng của tôi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            CartBottomSection(totalAmount)
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Giỏ hàng của bạn đang trống", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF7F7F7)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            val index = cartItems.indexOf(item)
                            if (index != -1) {
                                cartItems[index] = item.copy(quantity = item.quantity + 1)
                            }
                        },
                        onDecrease = {
                            if (item.quantity > 1) {
                                val index = cartItems.indexOf(item)
                                if (index != -1) {
                                    cartItems[index] = item.copy(quantity = item.quantity - 1)
                                }
                            }
                        },
                        onRemove = {
                            cartItems.remove(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CartBottomSection(totalAmount: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng cộng", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                Text(
                    text = String.format(Locale.getDefault(), "$%.2f", totalAmount),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00BE26)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* Xử lý thanh toán */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BE26))
            ) {
                Text("Thanh toán ngay", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    CartScreen()
}
