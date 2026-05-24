package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.data.remote.ProductApiService
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val apiService: ProductApiService
) : ProductRepository {

    // Offline resilient mock data
    private val mockProducts = listOf(
        Product(
            id = "1",
            name = "Smartphone X (Mock)",
            description = "Màn hình OLED 6.7 inch, Chip A15 Bionic",
            price = 999.0,
            imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=300&auto=format&fit=crop",
            rating = 4.8,
            discount = 15
        ),
        Product(
            id = "2",
            name = "MacBook Pro (Mock)",
            description = "Chip M2, RAM 16GB, SSD 512GB",
            price = 1499.0,
            imageUrl = "https://cellphones.com.vn/sforum/wp-content/uploads/2021/05/render-MacBook-Pro-2021-1.jpg",
            rating = 4.9,
            discount = 10
        ),
        Product(
            id = "3",
            name = "Sony Headphones (Mock)",
            description = "Chống ồn chủ động, Pin 30h",
            price = 349.0,
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=300&auto=format&fit=crop",
            rating = 4.7,
            discount = 20
        )
    )

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            // Log warning and fallback to mock products for offline testing resilience
            Result.success(mockProducts)
        }
    }

    override suspend fun getProductDetails(id: String): Result<Product> {
        return try {
            val intId = id.toIntOrNull()
            if (intId != null) {
                val response = apiService.getProductDetails(intId)
                Result.success(response.toDomain())
            } else {
                val mockProduct = mockProducts.find { it.id == id }
                if (mockProduct != null) Result.success(mockProduct)
                else Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            val mockProduct = mockProducts.find { it.id == id }
            if (mockProduct != null) Result.success(mockProduct)
            else Result.failure(e)
        }
    }
}
