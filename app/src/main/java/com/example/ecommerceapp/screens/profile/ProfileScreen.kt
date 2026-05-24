package com.example.ecommerceapp.screens.profile

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ecommerceapp.feature.profile.presentation.ProfileViewModel
import com.example.ecommerceapp.feature.profile.presentation.ProfileScreen as NewProfileScreen

@Composable
fun ProfileScreen(onSignOut: () -> Unit = {}) {
    val viewModel: ProfileViewModel = hiltViewModel()
    NewProfileScreen(
        viewModel = viewModel,
        onOrdersClick = {},
        onSignOutSuccess = onSignOut
    )
}
