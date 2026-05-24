package com.example.ecommerceapp.data.remote

import com.example.ecommerceapp.domain.model.Product
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val thumbnail: String,
    val rating: Double = 0.0,
    val discountPercentage: Double = 0.0
) {
    fun toDomain(): Product = Product(
        id = id.toString(),
        name = title,
        description = description,
        price = price,
        imageUrl = thumbnail,
        rating = rating,
        discount = discountPercentage.toInt(),
        category = category
    )
}

data class DummyJsonProductResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

interface ProductApiService {
    @GET("products")
    suspend fun getProducts(@Query("limit") limit: Int = 100): DummyJsonProductResponse

    @GET("products/{id}")
    suspend fun getProductDetails(@Path("id") id: Int): ProductDto
}
