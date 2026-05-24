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

    // Offline resilient mock data - 10 items covering all categories
    private val mockProducts = listOf(
        Product(
            id = "1",
            name = "Smartphone X (Mock)",
            description = "Màn hình OLED 6.7 inch, Chip A15 Bionic",
            price = 999.0,
            imageUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?q=80&w=300&auto=format&fit=crop",
            rating = 4.8,
            discount = 15,
            category = "smartphones"
        ),
        Product(
            id = "2",
            name = "MacBook Pro (Mock)",
            description = "Chip M2, RAM 16GB, SSD 512GB",
            price = 1499.0,
            imageUrl = "https://cellphones.com.vn/sforum/wp-content/uploads/2021/05/render-MacBook-Pro-2021-1.jpg",
            rating = 4.9,
            discount = 10,
            category = "laptops"
        ),
        Product(
            id = "3",
            name = "Sony Headphones (Mock)",
            description = "Chống ồn chủ động, Pin 30h",
            price = 349.0,
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?q=80&w=300&auto=format&fit=crop",
            rating = 4.7,
            discount = 20,
            category = "smartphones"
        ),
        Product(
            id = "4",
            name = "Áo khoác gió thời trang (Mock)",
            description = "Áo khoác chống nước, cản gió nhẹ nhàng thoáng khí",
            price = 45.0,
            imageUrl = "https://images.unsplash.com/photo-1551028719-00167b16eac5?q=80&w=300&auto=format&fit=crop",
            rating = 4.5,
            discount = 5,
            category = "tops"
        ),
        Product(
            id = "5",
            name = "Váy nữ xếp ly dáng dài (Mock)",
            description = "Chất liệu lụa Hàn sang trọng, xếp ly tinh tế",
            price = 68.0,
            imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?q=80&w=300&auto=format&fit=crop",
            rating = 4.6,
            discount = 12,
            category = "womens-dresses"
        ),
        Product(
            id = "6",
            name = "Đèn ngủ thông minh decor (Mock)",
            description = "Điều chỉnh độ sáng cảm ứng, ánh sáng vàng ấm áp",
            price = 25.0,
            imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?q=80&w=300&auto=format&fit=crop",
            rating = 4.4,
            discount = 8,
            category = "home-decoration"
        ),
        Product(
            id = "7",
            name = "Ghế Sofa thư giãn gỗ sồi (Mock)",
            description = "Thiết kế Bắc Âu tối giản, đệm cao su êm ái",
            price = 320.0,
            imageUrl = "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?q=80&w=300&auto=format&fit=crop",
            rating = 4.8,
            discount = 15,
            category = "furniture"
        ),
        Product(
            id = "8",
            name = "Nước hoa Lancome Pháp (Mock)",
            description = "Hương thơm ngọt ngào, lưu hương quyến rũ suốt 12h",
            price = 120.0,
            imageUrl = "https://images.unsplash.com/photo-1541643600914-78b084683601?q=80&w=300&auto=format&fit=crop",
            rating = 4.9,
            discount = 10,
            category = "fragrances"
        ),
        Product(
            id = "9",
            name = "Kem dưỡng ẩm chuyên sâu (Mock)",
            description = "Cung cấp HA và Vitamin E nuôi dưỡng da căng bóng",
            price = 35.0,
            imageUrl = "https://images.unsplash.com/photo-1556228720-195a672e8a03?q=80&w=300&auto=format&fit=crop",
            rating = 4.6,
            discount = 18,
            category = "beauty"
        ),
        Product(
            id = "10",
            name = "Giày Sneaker thể thao nam (Mock)",
            description = "Đế cao su đàn hồi cao, thiết kế ôm chân thoáng khí",
            price = 85.0,
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?q=80&w=300&auto=format&fit=crop",
            rating = 4.7,
            discount = 10,
            category = "mens-shoes"
        )
    )

    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            Result.success(response.products.map { it.toDomain() })
        } catch (e: Exception) {
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
