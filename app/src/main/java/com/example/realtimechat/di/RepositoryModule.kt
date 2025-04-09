package com.example.realtimechat.di

import com.example.realtimechat.data.repository.ChatRepositoryImpl
import com.example.realtimechat.data.repository.UserRepositoryImpl
import com.example.realtimechat.data.source.FirestoreUserDataSource
import com.example.realtimechat.domain.repository.ChatRepository
import com.example.realtimechat.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}