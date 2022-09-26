package com.example.runningevents.di

import android.app.Application
import android.content.SharedPreferences
import com.example.runningevents.data.remote.CitiesApiService
import com.example.runningevents.data.remote.PositionstackApiService
import com.example.runningevents.data.repositories.CitiesRepositoryImpl
import com.example.runningevents.data.repositories.ImagesStorageImpl
import com.example.runningevents.data.repositories.RacesRepositoryImpl
import com.example.runningevents.data.repositories.UserAuthenticationRepositoryImpl
import com.example.runningevents.domain.repositories.CitiesRepository
import com.example.runningevents.domain.repositories.ImagesStorage
import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideRacesRepository(
        sharedPreferences: SharedPreferences,
        application: Application,
        firestore: FirebaseFirestore
    ): RacesRepository {
        return RacesRepositoryImpl(
            sharedPreferences = sharedPreferences,
            application = application,
            firestore = firestore
        )
    }

    @Singleton
    @Provides
    fun provideImagesStorage(firebaseStorage: FirebaseStorage): ImagesStorage {
        return ImagesStorageImpl(firebaseStorage = firebaseStorage)
    }

    @Singleton
    @Provides
    fun provideCitiesRepository(
        citiesApi: CitiesApiService,
        positionstackApi: PositionstackApiService
    ): CitiesRepository {
        return CitiesRepositoryImpl(citiesApi = citiesApi, positionstackApi = positionstackApi)
    }

    @Singleton
    @Provides
    fun provideUserAuthenticationRepository(firebaseAuth: FirebaseAuth): UserAuthenticationRepository {
        return UserAuthenticationRepositoryImpl(authentication = firebaseAuth)
    }
}