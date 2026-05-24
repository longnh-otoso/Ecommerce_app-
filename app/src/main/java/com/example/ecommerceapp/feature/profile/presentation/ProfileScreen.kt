package com.example.ecommerceapp.feature.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ecommerceapp.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onOrdersClick: () -> Unit,
    onSignOutSuccess: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showEditProfileSheet by remember { mutableStateOf(false) }

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
                ProfileHeader(user = userProfile)
            }

            // Section: Account
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileSectionTitle("TÀI KHOẢN")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    title = "Thông tin cá nhân",
                    subtitle = if (!userProfile?.phoneNumber.isNullOrBlank()) userProfile?.phoneNumber else "Chưa hoàn thiện",
                    onClick = { showEditProfileSheet = true }
                )
                ProfileMenuItem(
                    icon = Icons.Default.ShoppingBag,
                    title = "Đơn hàng của tôi",
                    onClick = onOrdersClick
                )
                ProfileMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Địa chỉ nhận hàng",
                    subtitle = if (!userProfile?.address.isNullOrBlank()) userProfile?.address else "Chưa thiết lập",
                    onClick = { showEditProfileSheet = true }
                )
                ProfileMenuItem(
                    icon = Icons.Default.Payment,
                    title = "Phương thức thanh toán"
                )
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

    if (showEditProfileSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showEditProfileSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            EditProfileSheetContent(
                userProfile = userProfile,
                onSave = { name, phone, address, avatarUrl ->
                    viewModel.updateProfile(name, phone, address, avatarUrl)
                    showEditProfileSheet = false
                },
                onDismiss = {
                    showEditProfileSheet = false
                }
            )
        }
    }
}

@Composable
fun ProfileHeader(user: UserProfile?) {
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
                painter = rememberAsyncImagePainter(user?.profilePictureUrl ?: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=250"),
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
    subtitle: String? = null,
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
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF222222)
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
            
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

@Composable
fun EditProfileSheetContent(
    userProfile: UserProfile?,
    onSave: (String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(userProfile?.name ?: "") }
    var phone by remember { mutableStateOf(userProfile?.phoneNumber ?: "") }
    var address by remember { mutableStateOf(userProfile?.address ?: "") }
    var selectedAvatar by remember { mutableStateOf(userProfile?.profilePictureUrl ?: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=250") }

    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=250",
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?q=80&w=250",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=250",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=250",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=250"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(24.dp)
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hoàn thiện thông tin cá nhân",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar selector
        Text(
            text = "Chọn ảnh đại diện",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            presetAvatars.forEach { url ->
                val isSelected = selectedAvatar == url
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF1565C0) else Color.Transparent)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .clickable { selectedAvatar = url }
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(url),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
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

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hủy")
            }
            Button(
                onClick = { onSave(name, phone, address, selectedAvatar) },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
            ) {
                Text("Lưu thay đổi", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
