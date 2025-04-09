package com.example.realtimechat.di

import com.example.realtimechat.data.repository.AuthRepositoryImpl
import com.example.realtimechat.data.repository.ChatRepositoryImpl
import com.example.realtimechat.data.repository.FirestoreRepository
import com.example.realtimechat.data.repository.MessageRepositoryImpl
import com.example.realtimechat.data.source.remote.FirestoreMessageDataSource
import com.example.realtimechat.data.source.remote.FirestoreMessageDataSourceImpl
import com.example.realtimechat.domain.repository.AuthRepository
import com.example.realtimechat.domain.repository.ChatRepository
import com.example.realtimechat.domain.repository.MessageRepository
import com.example.realtimechat.domain.repository.UserRepository
import com.example.realtimechat.domain.usecase.GetAllUsersUseCase
import com.example.realtimechat.domain.usecase.GetCurrentUserUseCase
import com.example.realtimechat.domain.usecase.message.GetMessagesUseCase
import com.example.realtimechat.domain.usecase.message.MessageUseCases
import com.example.realtimechat.domain.usecase.message.SendMessageUseCase
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
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): AuthRepository = AuthRepositoryImpl(auth, firestore)

    @Provides
    @Singleton
    fun provideFirestoreRepository(db: FirebaseFirestore): FirestoreRepository =
        FirestoreRepository(db)

    @Provides
    @Singleton
    fun provideFirestoreMessageDataSource(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): FirestoreMessageDataSource = FirestoreMessageDataSourceImpl(firestore, storage)

    @Provides
    @Singleton
    fun provideMessageRepository(
        remoteDataSource: FirestoreMessageDataSource
    ): MessageRepository = MessageRepositoryImpl(remoteDataSource)

    @Provides
    @Singleton
    fun provideMessageUseCases(repository: MessageRepository): MessageUseCases {
        return MessageUseCases(
            getMessages = GetMessagesUseCase(repository),
            sendMessage = SendMessageUseCase(repository)
        )
    }

    @Provides
    fun provideGetAllUsersUseCase(repository: UserRepository): GetAllUsersUseCase {
        return GetAllUsersUseCase(repository)
    }

    @Provides
    fun provideGetCurrentUserUseCase(repository: UserRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepository = ChatRepositoryImpl(firestore)


}
