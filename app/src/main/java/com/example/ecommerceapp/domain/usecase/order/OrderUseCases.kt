package com.example.ecommerceapp.domain.usecase.order

import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Order
import com.example.ecommerceapp.domain.repository.OrderRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(
        userId: String,
        items: List<CartItem>,
        totalAmount: Double,
        paymentMethod: String = "COD",
        paymentStatus: String = "Chưa thanh toán"
    ): Result<Order> =
        repository.placeOrder(userId, items, totalAmount, paymentMethod, paymentStatus)
}

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Order>> = repository.getOrders(userId)
}
