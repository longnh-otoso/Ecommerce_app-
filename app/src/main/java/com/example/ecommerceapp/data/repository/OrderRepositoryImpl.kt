package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Order
import com.example.ecommerceapp.domain.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor() : OrderRepository {

    private var firestore: FirebaseFirestore? = null
    private val mockOrders = mutableListOf<Order>()

    init {
        try {
            firestore = FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            // Firebase is not initialized
        }
    }

    override suspend fun placeOrder(
        userId: String,
        items: List<CartItem>,
        totalAmount: Double,
        paymentMethod: String,
        paymentStatus: String
    ): Result<Order> {
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            items = items,
            totalAmount = totalAmount,
            timestamp = System.currentTimeMillis(),
            status = "Processing",
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus
        )
        
        val db = firestore
        return if (db != null) {
            try {
                // Submit order to Firestore
                val task = db.collection("orders").document(order.id).set(order)
                while (!task.isComplete) {
                    kotlinx.coroutines.delay(50)
                }
                
                if (task.isSuccessful) {
                    Result.success(order)
                } else {
                    Result.failure(task.exception ?: Exception("Failed to place order in Firestore"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Mock placing order
            mockOrders.add(order)
            Result.success(order)
        }
    }

    override suspend fun getOrders(userId: String): Result<List<Order>> {
        val db = firestore
        return if (db != null) {
            try {
                val task = db.collection("orders").whereEqualTo("userId", userId).get()
                while (!task.isComplete) {
                    kotlinx.coroutines.delay(50)
                }
                
                if (task.isSuccessful) {
                    val ordersList = task.result?.documents?.mapNotNull { doc ->
                        doc.toObject(Order::class.java)
                    } ?: emptyList()
                    Result.success(ordersList)
                } else {
                    Result.failure(task.exception ?: Exception("Failed to fetch orders"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Mock get orders
            Result.success(mockOrders.filter { it.userId == userId })
        }
    }
}
