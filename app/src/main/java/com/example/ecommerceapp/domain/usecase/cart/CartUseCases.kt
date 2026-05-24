package com.example.ecommerceapp.domain.usecase.cart

import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> = repository.getCartItems()
}

class AddToCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int) = repository.addToCart(product, quantity)
}

class UpdateCartQuantityUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(productId: String, quantity: Int) = repository.updateCartQuantity(productId, quantity)
}

class RemoveFromCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke(productId: String) = repository.removeFromCart(productId)
}

class ClearCartUseCase @Inject constructor(
    private val repository: CartRepository
) {
    suspend operator fun invoke() = repository.clearCart()
}
