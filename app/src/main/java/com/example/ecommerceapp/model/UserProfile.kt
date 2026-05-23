package com.example.ecommerceapp.model

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val address: String,
    val profilePictureUrl: String?

)

