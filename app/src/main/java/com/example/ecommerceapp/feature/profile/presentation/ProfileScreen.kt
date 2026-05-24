package com.example.ecommerceapp.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.ecommerceapp.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onOrdersClick: () -> Unit,
    onSignOutSuccess: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ của tôi", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Header: User Info
            item {
                ProfileHeader(user = currentUser)
            }

            // Section: Account
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileSectionTitle("TÀI KHOẢN")
            }

            item {
                ProfileMenuItem(icon = Icons.Default.Person, title = "Thông tin cá nhân")
                ProfileMenuItem(icon = Icons.Default.ShoppingBag, title = "Đơn hàng của tôi", onClick = onOrdersClick)
                ProfileMenuItem(icon = Icons.Default.LocationOn, title = "Địa chỉ nhận hàng")
                ProfileMenuItem(icon = Icons.Default.Payment, title = "Phương thức thanh toán")
            }

            // Section: Settings
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ProfileSectionTitle("CÀI ĐẶT")
            }

            item {
                ProfileMenuItem(icon = Icons.Default.Notifications, title = "Thông báo")
                ProfileMenuItem(icon = Icons.Default.Security, title = "Bảo mật")
                ProfileMenuItem(icon = Icons.Default.Help, title = "Trung tâm trợ giúp")
            }

            // Logout Button
            item {
                Spacer(modifier = Modifier.height(40.dp))
                LogoutButton {
                    viewModel.signOut(onSignOutSuccess)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=250"),
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = user?.name ?: "Khách hàng",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
        
        Text(
            text = user?.email ?: "chua_dang_nhap@email.com",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Color.Black,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF222222),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                color = Color(0xFF222222)
            )
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCCCCCC),
                modifier = Modifier.size(18.dp)
              )
        }
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 24.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF0F0F0)
    )
}

@Composable
fun LogoutButton(onSignOut: () -> Unit) {
    OutlinedButton(
        onClick = onSignOut,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(54.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Đăng xuất", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
