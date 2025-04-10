package com.example.realtimechat.di

import android.content.Context
import androidx.room.Room
import com.example.realtimechat.data.source.local.AppDatabase
import com.example.realtimechat.data.source.local.LocalMessageDataSource
import com.example.realtimechat.data.source.local.LocalMessageDataSourceImpl
import com.example.realtimechat.data.source.local.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chat-db"
        ).build()
    }


    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    fun provideLocalMessageDataSource(
        dao: MessageDao
    ): LocalMessageDataSource = LocalMessageDataSourceImpl(dao)

}
