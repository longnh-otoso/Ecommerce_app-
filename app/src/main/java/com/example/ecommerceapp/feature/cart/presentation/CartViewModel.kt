package com.example.ecommerceapp.feature.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.usecase.cart.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    getCartUseCase: GetCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = getCartUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalAmount: StateFlow<Double> = cartItems.map { list ->
        list.sumOf { item ->
            val finalPrice = if (item.product.discount > 0) {
                item.product.price * (1 - item.product.discount / 100.0)
            } else {
                item.product.price
            }
            finalPrice * item.quantity
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun increaseQuantity(productId: String, currentQuantity: Int) {
        viewModelScope.launch {
            updateCartQuantityUseCase(productId, currentQuantity + 1)
        }
    }

    fun decreaseQuantity(productId: String, currentQuantity: Int) {
        if (currentQuantity > 1) {
            viewModelScope.launch {
                updateCartQuantityUseCase(productId, currentQuantity - 1)
            }
        } else {
            removeItem(productId)
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            removeFromCartUseCase(productId)
        }
    }

    fun clear() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }
}
