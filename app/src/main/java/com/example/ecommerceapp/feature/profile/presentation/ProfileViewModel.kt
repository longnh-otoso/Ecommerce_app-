package com.example.ecommerceapp.feature.profile.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.User
import com.example.ecommerceapp.domain.usecase.auth.GetCurrentUserUseCase
import com.example.ecommerceapp.domain.usecase.auth.SignOutUseCase
import com.example.ecommerceapp.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val user = getCurrentUserUseCase()
        _currentUser.value = user
        if (user != null) {
            val savedName = sharedPreferences.getString("profile_${user.uid}_name", user.name) ?: user.name
            val savedPhone = sharedPreferences.getString("profile_${user.uid}_phone", "") ?: ""
            val savedAddress = sharedPreferences.getString("profile_${user.uid}_address", "") ?: ""
            val savedAvatar = sharedPreferences.getString("profile_${user.uid}_avatar", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=250") ?: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?q=80&w=250"
            _userProfile.value = UserProfile(
                uid = user.uid,
                name = savedName,
                email = user.email,
                phoneNumber = savedPhone,
                address = savedAddress,
                profilePictureUrl = savedAvatar
            )
        } else {
            _userProfile.value = null
        }
    }

    fun updateProfile(name: String, phone: String, address: String, avatarUrl: String) {
        val user = _currentUser.value ?: return
        sharedPreferences.edit().apply {
            putString("profile_${user.uid}_name", name)
            putString("profile_${user.uid}_phone", phone)
            putString("profile_${user.uid}_address", address)
            putString("profile_${user.uid}_avatar", avatarUrl)
            apply()
        }
        _userProfile.value = UserProfile(
            uid = user.uid,
            name = name,
            email = user.email,
            phoneNumber = phone,
            address = address,
            profilePictureUrl = avatarUrl
        )
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            signOutUseCase()
            _currentUser.value = null
            _userProfile.value = null
            onSuccess()
        }
    }
}
