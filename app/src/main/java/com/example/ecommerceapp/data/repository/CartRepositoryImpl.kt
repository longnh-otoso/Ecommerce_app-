package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.local.CartDao
import com.example.ecommerceapp.data.local.CartEntity
import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addToCart(product: Product, quantity: Int) {
        cartDao.insertCartItem(CartEntity.fromDomain(product, quantity))
    }

    override suspend fun updateCartQuantity(productId: String, quantity: Int) {
        cartDao.updateQuantity(productId, quantity)
    }

    override suspend fun removeFromCart(productId: String) {
        cartDao.deleteItem(productId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}
