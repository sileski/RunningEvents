package com.example.runningevents.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.runningevents.domain.location.LocationTracker
import com.example.runningevents.domain.location.LocationTrackerImpl
import com.example.runningevents.domain.repositories.ImagesStorage
import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.domain.repositories.UserAuthenticationRepository
import com.example.runningevents.domain.use_cases.*
import com.example.runningevents.domain.use_cases.validation.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Singleton
    @Provides
    fun provideDefaultSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    @Singleton
    @Provides
    fun funProvideFusedLocationProviderClient(application: Application): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }

    @Singleton
    @Provides
    fun provideLocationTracker(
        fusedLocationProviderClient: FusedLocationProviderClient,
        application: Application
    ): LocationTracker {
        return LocationTrackerImpl(
            locationClient = fusedLocationProviderClient,
            application = application
        )
    }

    @Singleton
    @Provides
    fun provideGetRacesByDateUseCase(racesRepository: RacesRepository): GetRacesByDateUseCase {
        return GetRacesByDateUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideGetRacesByLocationUseCase(racesRepository: RacesRepository): GetRacesByLocationUseCase {
        return GetRacesByLocationUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideGetMyRacesUseCase(racesRepository: RacesRepository): GetMyRacesUseCase {
        return GetMyRacesUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideAddNewRaceUseCase(racesRepository: RacesRepository): AddNewRaceUseCase {
        return AddNewRaceUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideDeleteRaceUseCase(racesRepository: RacesRepository): DeleteRaceUseCase {
        return DeleteRaceUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideGetRacesSortedBy(racesRepository: RacesRepository): GetRacesSortedByUseCase {
        return GetRacesSortedByUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideGetRacesRadiusDistance(racesRepository: RacesRepository): GetRacesRadiusDistance {
        return GetRacesRadiusDistance(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideGetRaceFilterDistancesUseCase(racesRepository: RacesRepository): GetRaceFilterDistancesUseCase {
        return GetRaceFilterDistancesUseCase(racesRepository = racesRepository)
    }

    @Singleton
    @Provides
    fun provideLogInWithEmailAndPasswordUseCase(userAuthenticationRepository: UserAuthenticationRepository): LogInWithEmailAndPasswordUseCase {
        return LogInWithEmailAndPasswordUseCase(repository = userAuthenticationRepository)
    }

    @Singleton
    @Provides
    fun provideSignUpWithEmailAndPasswordUseCase(userAuthenticationRepository: UserAuthenticationRepository): SignUpWithEmailAndPasswordUseCase {
        return SignUpWithEmailAndPasswordUseCase(repository = userAuthenticationRepository)
    }

    @Singleton
    @Provides
    fun provideLogInWithGoogleUseCase(userAuthenticationRepository: UserAuthenticationRepository): LogInWithGoogleUseCase {
        return LogInWithGoogleUseCase(repository = userAuthenticationRepository)
    }

    @Singleton
    @Provides
    fun provideLogInAnonymouslyUseCase(userAuthenticationRepository: UserAuthenticationRepository): LogInAnonymouslyUseCase {
        return LogInAnonymouslyUseCase(repository = userAuthenticationRepository)
    }

    @Singleton
    @Provides
    fun provideGetUserUseCase(userAuthenticationRepository: UserAuthenticationRepository): GetUserUseCase {
        return GetUserUseCase(repository = userAuthenticationRepository)
    }

    @Singleton
    @Provides
    fun provideLogOutUseCase(userAuthenticationRepository: UserAuthenticationRepository): LogOutUseCase {
        return LogOutUseCase(repository = userAuthenticationRepository)
    }

    @Provides
    @Singleton
    fun provideSaveImageUseCase(imagesStorage: ImagesStorage): SaveImageUseCase {
        return SaveImageUseCase(imagesStorage = imagesStorage)
    }

    @Singleton
    @Provides
    fun provideValidateCityUseCase(): ValidateCityUseCase {
        return ValidateCityUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateCountryUseCase(): ValidateCountryUseCase {
        return ValidateCountryUseCase()
    }


    @Singleton
    @Provides
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase {
        return ValidatePasswordUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateConfirmPasswordUseCase(): ValidateConfirmPasswordUseCase {
        return ValidateConfirmPasswordUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateEmailUseCase(): ValidateEmailUseCase {
        return ValidateEmailUseCase()
    }

    @Singleton
    @Provides
    fun validateRaceDateUseCase(): ValidateRaceDateUseCase {
        return ValidateRaceDateUseCase()
    }

    @Singleton
    @Provides
    fun validateRaceTimeUseCase(): ValidateRaceTimeUseCase {
        return ValidateRaceTimeUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateFullNameUseCase(): ValidateFullNameUseCase {
        return ValidateFullNameUseCase()
    }


    @Singleton
    @Provides
    fun provideValidateRaceNameUseCase(): ValidateRaceNameUseCase {
        return ValidateRaceNameUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateWebsiteUseCase(): ValidateWebsiteUseCase {
        return ValidateWebsiteUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateRaceDescriptionUseCase(): ValidateRaceDescriptionUseCase {
        return ValidateRaceDescriptionUseCase()
    }

    @Singleton
    @Provides
    fun provideValidateImageUriUseCase(): ValidateImageUriUseCase {
        return ValidateImageUriUseCase()
    }
}