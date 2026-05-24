package com.example.ecommerceapp.feature.order.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.ecommerceapp.domain.model.CartItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    totalAmount: Double,
    userId: String,
    orderViewModel: OrderViewModel,
    onBackClick: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE) }

    // Delivery info states, loaded from Profile SharedPreferences
    var name by remember { mutableStateOf(sharedPreferences.getString("profile_${userId}_name", "") ?: "") }
    var phone by remember { mutableStateOf(sharedPreferences.getString("profile_${userId}_phone", "") ?: "") }
    var address by remember { mutableStateOf(sharedPreferences.getString("profile_${userId}_address", "") ?: "") }

    // Payment states
    var selectedMethod by remember { mutableStateOf("COD") } // "COD", "BANK", "MOMO", "CARD"
    
    // Credit card states
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCVV by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }

    val uiState by orderViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is OrderUiState.Success) {
            // Save updated delivery info back to shared prefs
            sharedPreferences.edit().apply {
                putString("profile_${userId}_name", name)
                putString("profile_${userId}_phone", phone)
                putString("profile_${userId}_address", address)
                apply()
            }
            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
            onCheckoutSuccess()
        } else if (uiState is OrderUiState.Error) {
            Toast.makeText(context, (uiState as OrderUiState.Error).message, Toast.LENGTH_LONG).show()
            orderViewModel.clearState()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thanh toán đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Button(
                    onClick = {
                        if (name.isBlank() || phone.isBlank() || address.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show()
                        } else if (selectedMethod == "CARD" && (cardNumber.length < 12 || cardExpiry.length < 4 || cardCVV.length < 3)) {
                            Toast.makeText(context, "Vui lòng nhập thông tin thẻ hợp lệ", Toast.LENGTH_SHORT).show()
                        } else {
                            val payStatus = if (selectedMethod == "COD") {
                                "Chưa thanh toán"
                            } else {
                                "Đã thanh toán"
                            }
                            orderViewModel.checkout(userId, cartItems, totalAmount, selectedMethod, payStatus)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BE26)),
                    enabled = uiState !is OrderUiState.Loading
                ) {
                    if (uiState is OrderUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Xác nhận & Thanh toán (${String.format(Locale.getDefault(), "$%.2f", totalAmount)})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Delivery Information Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF1565C0))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thông tin giao hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Tên người nhận") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Số điện thoại") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Địa chỉ nhận hàng") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // 2. Payment Method Selection Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color(0xFF1565C0))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Phương thức thanh toán", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // COD Option
                        PaymentMethodRow(
                            title = "Thanh toán khi nhận hàng (COD)",
                            subtitle = "Thanh toán bằng tiền mặt khi giao hàng",
                            selected = selectedMethod == "COD",
                            onClick = { selectedMethod = "COD" }
                        )
                        
                        // Bank Transfer Option
                        PaymentMethodRow(
                            title = "Chuyển khoản ngân hàng",
                            subtitle = "Hỗ trợ quét QR nhanh chóng",
                            selected = selectedMethod == "BANK",
                            onClick = { selectedMethod = "BANK" }
                        )

                        // Momo Option
                        PaymentMethodRow(
                            title = "Ví điện tử MoMo",
                            subtitle = "Thanh toán qua ứng dụng MoMo",
                            selected = selectedMethod == "MOMO",
                            onClick = { selectedMethod = "MOMO" }
                        )

                        // Credit Card Option
                        PaymentMethodRow(
                            title = "Thẻ quốc tế Visa / Mastercard",
                            subtitle = "Liên kết thẻ tín dụng hoặc ghi nợ",
                            selected = selectedMethod == "CARD",
                            onClick = { selectedMethod = "CARD" }
                        )
                    }
                }
            }

            // 3. Dynamic Payment Details View
            item {
                when (selectedMethod) {
                    "BANK" -> {
                        BankTransferDetailView(totalAmount)
                    }
                    "MOMO" -> {
                        MomoDetailView(totalAmount)
                    }
                    "CARD" -> {
                        CardDetailView(
                            cardNumber = cardNumber,
                            onCardNumberChange = { cardNumber = it.take(16).filter { char -> char.isDigit() } },
                            cardExpiry = cardExpiry,
                            onCardExpiryChange = { cardExpiry = it.take(5) },
                            cardCVV = cardCVV,
                            onCardCVVChange = { cardCVV = it.take(3).filter { char -> char.isDigit() } },
                            cardHolder = cardHolder,
                            onCardHolderChange = { cardHolder = it }
                        )
                    }
                }
            }

            // 4. Order Summary Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tóm tắt sản phẩm", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF9F9F9))
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(item.product.imageUrl),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.name, fontWeight = FontWeight.SemiBold, maxLines = 1, fontSize = 14.sp)
                                    Text("Số lượng: x${item.quantity}", color = Color.Gray, fontSize = 12.sp)
                                }
                                Text(
                                    text = String.format(Locale.getDefault(), "$%.2f", item.product.price * item.quantity),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Phí giao hàng", color = Color.Gray, fontSize = 14.sp)
                            Text("Miễn phí", color = Color(0xFF00BE26), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1565C0))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (selected) Color(0xFF1565C0) else Color.Black)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BankTransferDetailView(amount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFF1565C0))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thông tin chuyển khoản", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock MB Bank QR image using standard online QR generator
            val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=STK_999988887777_MBBANK_AMOUNT_${amount}"
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(qrUrl),
                    contentDescription = "Bank Transfer QR Code",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Ngân hàng:", color = Color.Gray, fontSize = 14.sp)
                Text("MB Bank (Quân Đội)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Số tài khoản:", color = Color.Gray, fontSize = 14.sp)
                Text("9999 8888 7777", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1565C0))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Chủ tài khoản:", color = Color.Gray, fontSize = 14.sp)
                Text("CONG TY ECOMMERCE VIETNAM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nội dung CK:", color = Color.Gray, fontSize = 14.sp)
                Text("THANH TOAN DON HANG", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00BE26))
            }
        }
    }
}

@Composable
fun MomoDetailView(amount: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFA50064))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thanh toán qua ví MoMo", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFA50064))
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mock Momo QR Code
            val qrUrl = "https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=MOMO_WALLET_0999888777_AMOUNT_${amount}"
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(qrUrl),
                    contentDescription = "MoMo QR Code",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Quét mã QR trên để hoàn tất thanh toán MoMo",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CardDetailView(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit,
    cardExpiry: String,
    onCardExpiryChange: (String) -> Unit,
    cardCVV: String,
    onCardCVVChange: (String) -> Unit,
    cardHolder: String,
    onCardHolderChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Interactive Credit Card visual
            CreditCardVisual(cardNumber, cardExpiry, cardHolder)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberChange,
                label = { Text("Số thẻ (16 chữ số)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = cardHolder,
                onValueChange = onCardHolderChange,
                label = { Text("Tên in trên thẻ") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = cardExpiry,
                    onValueChange = onCardExpiryChange,
                    label = { Text("Hạn dùng (MM/YY)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("12/28") }
                )
                OutlinedTextField(
                    value = cardCVV,
                    onValueChange = onCardCVVChange,
                    label = { Text("Mã CVV (3 số)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}

@Composable
fun CreditCardVisual(cardNumber: String, cardExpiry: String, cardHolder: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF1E3C72), Color(0xFF2A5298))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GOLDEN CARD",
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            val displayCardNumber = if (cardNumber.isEmpty()) {
                "•••• •••• •••• ••••"
            } else {
                cardNumber.chunked(4).joinToString(" ")
            }
            Text(
                text = displayCardNumber,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CARD HOLDER",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                    Text(
                        text = if (cardHolder.isEmpty()) "NGUYEN VAN A" else cardHolder.uppercase(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "EXPIRES",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp
                    )
                    Text(
                        text = if (cardExpiry.isEmpty()) "MM/YY" else cardExpiry,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
