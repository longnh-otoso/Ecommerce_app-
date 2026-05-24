package com.example.ecommerceapp.domain.usecase.order

import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Order
import com.example.ecommerceapp.domain.repository.OrderRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(userId: String, items: List<CartItem>, totalAmount: Double): Result<Order> =
        repository.placeOrder(userId, items, totalAmount)
}

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Order>> = repository.getOrders(userId)
}
