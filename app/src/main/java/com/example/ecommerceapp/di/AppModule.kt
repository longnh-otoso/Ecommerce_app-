package com.example.ecommerceapp.di

import android.content.Context
import androidx.room.Room
import com.example.ecommerceapp.data.local.CartDao
import com.example.ecommerceapp.data.local.CartDatabase
import com.example.ecommerceapp.data.remote.ProductApiService
import com.example.ecommerceapp.data.repository.AuthRepositoryImpl
import com.example.ecommerceapp.data.repository.CartRepositoryImpl
import com.example.ecommerceapp.data.repository.OrderRepositoryImpl
import com.example.ecommerceapp.data.repository.ProductRepositoryImpl
import com.example.ecommerceapp.domain.repository.AuthRepository
import com.example.ecommerceapp.domain.repository.CartRepository
import com.example.ecommerceapp.domain.repository.OrderRepository
import com.example.ecommerceapp.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCartDatabase(@ApplicationContext context: Context): CartDatabase {
        return Room.databaseBuilder(
            context,
            CartDatabase::class.java,
            "cart_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideCartDao(db: CartDatabase): CartDao {
        return db.cartDao()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideProductRepository(impl: ProductRepositoryImpl): ProductRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideCartRepository(impl: CartRepositoryImpl): CartRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideOrderRepository(impl: OrderRepositoryImpl): OrderRepository {
        return impl
    }
}
