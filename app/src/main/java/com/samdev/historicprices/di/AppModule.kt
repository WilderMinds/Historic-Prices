package com.samdev.historicprices.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.samdev.historicprices.AppConstants
import com.samdev.historicprices.data.local.AppDatabase
import com.samdev.historicprices.data.local.preferences.IPrefManager
import com.samdev.historicprices.data.local.preferences.PrefManager
import com.samdev.historicprices.data.remote.ApiService
import com.samdev.historicprices.data.repositories.ProductRepository
import com.samdev.historicprices.data.repositories.IProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * @author Sam
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideProductDao(
        database: AppDatabase
    ) = database.productDao()

    @Singleton
    @Provides
    fun providePriceDao(
        database: AppDatabase
    ) = database.priceDao()

    @Singleton
    @Provides
    fun provideProductWithPricesDao(
        database: AppDatabase
    ) = database.productWithPricesDao()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()


    @Singleton
    @Provides
    fun provideRetrofit(
        gson: Gson,
        client: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(AppConstants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()


    @Singleton
    @Provides
    fun provideOKHTTP(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        return httpClient.build()
    }


    @Provides
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create(
        ApiService::class.java
    )


    @Singleton
    @Provides
    fun provideDefaultProductRepository(
        database: AppDatabase,
        apiService: ApiService
    ) = ProductRepository(database, apiService) as IProductRepository

    @Singleton
    @Provides
    fun providePrefManager(
        @ApplicationContext context: Context,
    ) = PrefManager(context) as IPrefManager
}