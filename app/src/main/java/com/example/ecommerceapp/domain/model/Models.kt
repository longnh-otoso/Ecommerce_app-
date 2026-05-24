package com.example.ecommerceapp.domain.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val discount: Int = 0,
    val category: String = ""
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = ""
)

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val timestamp: Long = 0,
    val status: String = "Pending",
    val paymentMethod: String = "COD",
    val paymentStatus: String = "Chưa thanh toán"
)
