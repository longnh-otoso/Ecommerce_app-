package com.example.ecommerceapp.screens.cart

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.feature.cart.presentation.CartViewModel
import com.example.ecommerceapp.feature.cart.presentation.CartScreen as NewCartScreen

@Composable
fun CartScreen(
    onBackClick: () -> Unit = {}
) {
    val viewModel: CartViewModel = hiltViewModel()
    NewCartScreen(
        viewModel = viewModel,
        onBackClick = onBackClick,
        onCheckoutClick = {}
    )
}
