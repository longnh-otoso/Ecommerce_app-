package com.example.ecommerceapp.domain.usecase.product

import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(): Result<List<Product>> = repository.getProducts()
}

class GetProductDetailsUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(id: String): Result<Product> = repository.getProductDetails(id)
}
