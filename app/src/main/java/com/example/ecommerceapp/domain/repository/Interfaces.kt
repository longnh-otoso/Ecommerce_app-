package com.example.ecommerceapp.domain.repository

import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Order
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, name: String, password: String): Result<User>
    suspend fun signOut()
    fun getCurrentUser(): User?
    fun getAuthState(): Flow<User?>
}

interface ProductRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun getProductDetails(id: String): Result<Product>
}

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(product: Product, quantity: Int)
    suspend fun updateCartQuantity(productId: String, quantity: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()
}

interface OrderRepository {
    suspend fun placeOrder(userId: String, items: List<CartItem>, totalAmount: Double): Result<Order>
    suspend fun getOrders(userId: String): Result<List<Order>>
}
