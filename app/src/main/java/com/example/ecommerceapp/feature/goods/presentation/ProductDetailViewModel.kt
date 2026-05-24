package com.example.ecommerceapp.feature.goods.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.Product
import com.example.ecommerceapp.domain.usecase.cart.AddToCartUseCase
import com.example.ecommerceapp.domain.usecase.product.GetProductDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductDetailUiState {
    object Loading : ProductDetailUiState
    data class Success(val product: Product) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading
            getProductDetailsUseCase(productId)
                .onSuccess { product ->
                    _uiState.value = ProductDetailUiState.Success(product)
                }
                .onFailure { error ->
                    _uiState.value = ProductDetailUiState.Error(error.message ?: "Không thể tải chi tiết sản phẩm")
                }
        }
    }

    fun increaseQuantity() {
        _quantity.value += 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    fun addToCart(product: Product, onSuccess: () -> Unit) {
        viewModelScope.launch {
            addToCartUseCase(product, _quantity.value)
            onSuccess()
        }
    }
}
