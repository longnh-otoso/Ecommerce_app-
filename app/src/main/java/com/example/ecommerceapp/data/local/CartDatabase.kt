package com.example.ecommerceapp.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.example.ecommerceapp.domain.model.CartItem
import com.example.ecommerceapp.domain.model.Product
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val discount: Int,
    val quantity: Int
) {
    fun toDomain(): CartItem = CartItem(
        product = Product(
            id = productId,
            name = name,
            price = price,
            imageUrl = imageUrl,
            discount = discount
        ),
        quantity = quantity
    )

    companion object {
        fun fromDomain(product: Product, quantity: Int): CartEntity = CartEntity(
            productId = product.id,
            name = product.name,
            price = product.price,
            imageUrl = product.imageUrl,
            discount = product.discount,
            quantity = quantity
        )
    }
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartEntity: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Database(entities = [CartEntity::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
