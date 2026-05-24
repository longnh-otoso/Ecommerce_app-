package com.example.ecommerceapp.feature.order.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecommerceapp.domain.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    userId: String,
    viewModel: OrderViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val ordersList by viewModel.ordersList.collectAsState()
    val isLoadingOrders by viewModel.isLoadingOrders.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadOrders(userId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đơn hàng của tôi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Quay lại")
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
            when (uiState) {
                is OrderUiState.Loading -> {
                    CircularProgressIndicator(
                        color = Color(0xFF1565C0),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is OrderUiState.Success -> {
                    val order = (uiState as OrderUiState.Success).order
                    SuccessOrderDialog(
                        order = order,
                        onDismiss = {
                            viewModel.clearState()
                        }
                    )
                }
                is OrderUiState.Error -> {
                    val message = (uiState as OrderUiState.Error).message
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    if (isLoadingOrders) {
                        CircularProgressIndicator(
                            color = Color(0xFF1565C0),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else if (ordersList.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Bạn chưa có đơn hàng nào", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(ordersList) { order ->
                                OrderItemCard(order = order)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    val date = Date(order.timestamp)
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateString = format.format(date)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mã ĐH: #${order.id.take(8)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = order.status,
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Thời gian: $dateString",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Render brief items list
            order.items.forEach { item ->
                Text(
                    text = "${item.product.name} x${item.quantity}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng thanh toán", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = String.format(Locale.getDefault(), "$%.2f", order.totalAmount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF00BE26)
                )
            }
        }
    }
}

@Composable
fun SuccessOrderDialog(order: Order, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF00BE26),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Đặt hàng thành công!",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF222222)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Đơn hàng #${order.id.take(8)} của bạn đang được xử lý.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Đồng ý", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
