package com.example.ecommerceapp.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyTopAppBar(
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
){
    // Dùng Surface thay vì TopAppBar chuẩn để tùy biến chiều cao (Height) nhỏ hơn
    Surface(
        color = Color(0xFF1565c0),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding() // Đảm bảo không bị lút vào thanh trạng thái pin/sóng
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Chỉnh chiều cao nhỏ lại ở đây (chuẩn là 64dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ShopEasy",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp // Chữ nhỏ lại một chút cho cân đối
                )
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCartClick) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Cart",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
