package com.example.ecommerceapp.data.remote

import com.example.ecommerceapp.domain.model.Product
import retrofit2.http.GET
import retrofit2.http.Path

data class RatingDto(
    val rate: Double = 0.0,
    val count: Int = 0
)

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto?
) {
    fun toDomain(): Product = Product(
        id = id.toString(),
        name = title,
        description = description,
        price = price,
        imageUrl = image,
        rating = rating?.rate ?: 0.0,
        discount = if (id % 2 == 0) 15 else 0 // Generate mock discount
    )
}

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: Int): ProductDto
}
