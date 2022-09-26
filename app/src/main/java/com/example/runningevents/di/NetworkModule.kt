package com.example.runningevents.di

import com.example.runningevents.BuildConfig
import com.example.runningevents.data.remote.CitiesApiService
import com.example.runningevents.data.remote.PositionstackApiService
import com.example.runningevents.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Singleton
    @Provides
    fun provideOkhttp(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) {
                addNetworkInterceptor(loggingInterceptor)
            }
        }.build()
    }

    @Singleton
    @Named("CITIES_API")
    @Provides
    fun provideRetrofitCities(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.CITIES_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun citiesApi(@Named("CITIES_API") retrofit: Retrofit): CitiesApiService {
        return retrofit.create(CitiesApiService::class.java)
    }

    @Singleton
    @Named("POSITIONSTACK_API")
    @Provides
    fun provideRetrofitPositionstack(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.POSITIONSTACK_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun providePositionstackApi(@Named("POSITIONSTACK_API") retrofit: Retrofit): PositionstackApiService {
        return retrofit.create(PositionstackApiService::class.java)
    }

}