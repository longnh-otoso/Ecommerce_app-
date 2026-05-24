package com.example.ecommerceapp.data.repository

import android.util.Log
import com.example.ecommerceapp.model.Category
import com.example.ecommerceapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getCategoriesFlow(): Flow<List<Category>> =
        callbackFlow {
            val listenerRegistration = firestore.collection("categories")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val categories = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Category::class.java)
                    } ?: emptyList()
                }
            awaitClose { listenerRegistration.remove() }

        }

    suspend fun getProductByCategories( categoryId : String): List<Product> {
        return try{
            val result = firestore.collection("products")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await() // doi khong dong bo tranh chan luog giao dien
            result.toObjects(Product::class.java).also {
                Log.v( "ProductRepositoryImpl", "getProductByCategories: $it")
            }
        } catch (e: Exception) {
            emptyList()
        }

    }

  //  suspend fun getProdctByID

}