package com.example.ecommerceapp.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.User
import com.example.ecommerceapp.domain.usecase.auth.GetCurrentUserUseCase
import com.example.ecommerceapp.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _currentUser.value = getCurrentUserUseCase()
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
            onSuccess()
        }
    }
}
