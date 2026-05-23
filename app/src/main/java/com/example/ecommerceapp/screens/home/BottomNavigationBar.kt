package com.example.ecommerceapp.screens.home

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
){

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            icon = Icons.Default.Home,
            route = "Home"
        ),
        BottomNavigationItem(
            title = "Categories",
            icon = Icons.Default.Search,
            route = "Categories"
        ),
        BottomNavigationItem(
            title = "WishList",
            icon = Icons.Default.Favorite,
            route = "WishList",
            badgeCount = 5
        ),
        BottomNavigationItem(
            title = "Cart",
            icon = Icons.Default.ShoppingCart,
            route = "Cart",
            badgeCount = 3
        ),
        BottomNavigationItem(
            title = "Profile",
            icon = Icons.Default.Person,
            route = "Profile"
        )
    )

    NavigationBar(
        modifier = Modifier.height(100.dp),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected  = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                label = { Text(text = item.title) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount != null && item.badgeCount > 0) {
                                Badge {
                                    Text(text = item.badgeCount.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    }
                }
            )
        }
    }
}

data class BottomNavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int? = null
)


