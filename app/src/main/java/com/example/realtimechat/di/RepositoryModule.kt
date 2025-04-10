package com.example.realtimechat.di

import com.example.realtimechat.data.repository.MessageRepositoryImpl
import com.example.realtimechat.data.repository.UserRepositoryImpl
import com.example.realtimechat.data.source.FirestoreUserDataSource
import com.example.realtimechat.data.source.local.LocalMessageDataSource
import com.example.realtimechat.data.source.remote.FirestoreMessageDataSource
import com.example.realtimechat.domain.repository.MessageRepository
import com.example.realtimechat.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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

    @Module
    @InstallIn(SingletonComponent::class)
    object RepositoryModule {

        @Provides
        @Singleton
        fun provideMessageRepository(
            remoteDataSource: FirestoreMessageDataSource,
            localDataSource: LocalMessageDataSource
        ): MessageRepository {
            return MessageRepositoryImpl(
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource
            )
        }
    }

}
