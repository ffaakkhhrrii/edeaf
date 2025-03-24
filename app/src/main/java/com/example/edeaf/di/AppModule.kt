package com.example.edeaf.di

import com.example.edeaf.data.repository.DictionaryRepository
import com.example.edeaf.data.repository.LiveTransRepository
import com.example.edeaf.data.repository.LoginRepository
import com.example.edeaf.data.repository.ProfileRepository
import com.example.edeaf.data.repository.SignUpRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideFirebase() : FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRef():FirebaseDatabase{
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseUser(firebaseAuth: FirebaseAuth):FirebaseUser{
        return firebaseAuth.currentUser!!
    }

    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth):LoginRepository{
        return LoginRepository(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideSignupRepository(firebaseAuth: FirebaseAuth,firebaseDatabase: FirebaseDatabase):SignUpRepository{
        return SignUpRepository(firebaseAuth,firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideDictionaryRepository(firebaseDatabase: FirebaseDatabase):DictionaryRepository{
        return DictionaryRepository(firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideLiveTransRepository(firebaseAuth: FirebaseAuth,firebaseDatabase: FirebaseDatabase):LiveTransRepository{
        return LiveTransRepository(firebaseAuth, firebaseDatabase)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(firebaseAuth: FirebaseAuth,firebaseDatabase: FirebaseDatabase,firebaseUser: FirebaseUser):ProfileRepository{
        return ProfileRepository(firebaseAuth, firebaseDatabase,firebaseUser)
    }
}