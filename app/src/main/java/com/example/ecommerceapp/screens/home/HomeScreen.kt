package com.example.ecommerceapp.screens.home

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.feature.home.presentation.HomeViewModel
import com.example.ecommerceapp.feature.home.presentation.HomeScreen as NewHomeScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSeeAllCategoriesClick: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    NewHomeScreen(
        viewModel = viewModel,
        onProductClick = {},
        onCartClick = onCartClick,
        onProfileClick = onProfileClick
    )
}
