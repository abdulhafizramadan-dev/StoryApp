package com.ahr.storyapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ahr.storyapp.data.local.entity.RemoteKeys
import com.ahr.storyapp.data.local.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKeys::class], version = 2)
abstract class StoryDatabase : RoomDatabase() {

    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

}