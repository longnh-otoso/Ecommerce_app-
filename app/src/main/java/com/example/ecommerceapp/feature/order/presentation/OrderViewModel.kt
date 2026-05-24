package com.example.ecommerceapp.feature.order.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Order
import com.example.ecommerceapp.domain.usecase.cart.ClearCartUseCase
import com.example.ecommerceapp.domain.usecase.order.GetOrdersUseCase
import com.example.ecommerceapp.domain.usecase.order.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OrderUiState {
    object Idle : OrderUiState
    object Loading : OrderUiState
    data class Success(val order: Order) : OrderUiState
    data class Error(val message: String) : OrderUiState
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val getOrdersUseCase: GetOrdersUseCase,
    private val clearCartUseCase: ClearCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Idle)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    private val _ordersList = MutableStateFlow<List<Order>>(emptyList())
    val ordersList: StateFlow<List<Order>> = _ordersList.asStateFlow()

    private val _isLoadingOrders = MutableStateFlow(false)
    val isLoadingOrders: StateFlow<Boolean> = _isLoadingOrders.asStateFlow()

    fun loadOrders(userId: String) {
        viewModelScope.launch {
            _isLoadingOrders.value = true
            getOrdersUseCase(userId)
                .onSuccess { list ->
                    _ordersList.value = list
                }
                .onFailure {
                    // Ignore or show empty list
                }
            _isLoadingOrders.value = false
        }
    }

    fun checkout(
        userId: String,
        items: List<CartItem>,
        totalAmount: Double,
        paymentMethod: String = "COD",
        paymentStatus: String = "Chưa thanh toán"
    ) {
        if (items.isEmpty()) {
            _uiState.value = OrderUiState.Error("Không có sản phẩm nào để thanh toán")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            placeOrderUseCase(userId, items, totalAmount, paymentMethod, paymentStatus)
                .onSuccess { order ->
                    clearCartUseCase() // Clear local room cart
                    _uiState.value = OrderUiState.Success(order)
                    loadOrders(userId) // Reload order list
                }
                .onFailure { error ->
                    _uiState.value = OrderUiState.Error(error.message ?: "Thanh toán thất bại")
                }
        }
    }

    fun clearState() {
        _uiState.value = OrderUiState.Idle
    }
}
