package com.example.ecommerceapp.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.User
import com.example.ecommerceapp.domain.usecase.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        _currentUser.value = getCurrentUserUseCase()
        
        viewModelScope.launch {
            getAuthStateUseCase().collect { user ->
                _currentUser.value = user
                if (user != null) {
                    _uiState.value = AuthUiState.Success(user)
                } else {
                    _uiState.value = AuthUiState.Idle
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email và mật khẩu không được để trống")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            loginUseCase(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Đăng nhập thất bại")
                }
        }
    }

    fun register(email: String, name: String, password: String) {
        if (email.isBlank() || name.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Vui lòng điền đầy đủ các thông tin")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            registerUseCase(email, name, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Đăng ký thất bại")
                }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            signInWithGoogleUseCase(idToken)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Success(user)
                }
                .onFailure { exception ->
                    _uiState.value = AuthUiState.Error(exception.message ?: "Đăng nhập Google thất bại")
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
            _uiState.value = AuthUiState.Idle
        }
    }
    
    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}
