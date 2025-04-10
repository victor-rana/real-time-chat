package com.example.realtimechat.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.realtimechat.data.source.local.dao.MessageDao
import com.example.realtimechat.data.source.local.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
