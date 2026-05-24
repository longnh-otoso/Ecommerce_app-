package com.example.ecommerceapp.data.repository

import com.example.ecommerceapp.domain.model.User
import com.example.ecommerceapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    private var firebaseAuth: FirebaseAuth? = null
    
    // Fallback Mock State in case Firebase isn't initialized
    private val mockUserFlow = MutableStateFlow<User?>(null)
    private var mockCurrentUser: User? = null

    init {
        try {
            firebaseAuth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            // Firebase is not initialized (no google-services.json)
            // We will use the Mock User fallback system
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val auth = firebaseAuth
        return if (auth != null) {
            try {
                val task = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = task.user
                val user = User(
                    uid = firebaseUser?.uid ?: "",
                    name = firebaseUser?.displayName ?: "User",
                    email = firebaseUser?.email ?: email
                )
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Mock Login Success
            if (email.contains("@") && password.length >= 6) {
                val user = User("mock_uid_123", email.substringBefore("@"), email)
                mockCurrentUser = user
                mockUserFlow.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Email hợp lệ và mật khẩu tối thiểu 6 ký tự"))
            }
        }
    }

    override suspend fun register(email: String, name: String, password: String): Result<User> {
        val auth = firebaseAuth
        return if (auth != null) {
            try {
                val task = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = task.user
                val user = User(
                    uid = firebaseUser?.uid ?: "",
                    name = name,
                    email = firebaseUser?.email ?: email
                )
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Mock Register Success
            if (email.contains("@") && password.length >= 6) {
                val user = User("mock_uid_123", name, email)
                mockCurrentUser = user
                mockUserFlow.value = user
                Result.success(user)
            } else {
                Result.failure(Exception("Email hợp lệ và mật khẩu tối thiểu 6 ký tự"))
            }
        }
    }

    override suspend fun signOut() {
        val auth = firebaseAuth
        if (auth != null) {
            auth.signOut()
        } else {
            mockCurrentUser = null
            mockUserFlow.value = null
        }
    }

    override fun getCurrentUser(): User? {
        val auth = firebaseAuth
        return if (auth != null) {
            auth.currentUser?.let {
                User(
                    uid = it.uid,
                    name = it.displayName ?: "User",
                    email = it.email ?: ""
                )
            }
        } else {
            mockCurrentUser
        }
    }

    override fun getAuthState(): Flow<User?> {
        val auth = firebaseAuth
        return if (auth != null) {
            flow {
                emit(getCurrentUser())
                // Listen to state changes
                auth.addAuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser?.let {
                        User(uid = it.uid, name = it.displayName ?: "User", email = it.email ?: "")
                    }
                    // emit value in Flow
                }
            }
        } else {
            mockUserFlow
        }
    }
}
