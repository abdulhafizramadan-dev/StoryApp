package com.ahr.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.ahr.storyapp.data.local.datastore.AuthPreferences
import com.ahr.storyapp.data.local.datastore.datastore
import com.ahr.storyapp.data.local.database.StoryDatabase
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
    @Singleton
    fun provideAuthPreferences(datastore: DataStore<Preferences>): AuthPreferences {
        return AuthPreferences(datastore)
    }

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.datastore
    }

    @Provides
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase =
        Room.databaseBuilder(context.applicationContext, StoryDatabase::class.java, "story_database")
            .fallbackToDestructiveMigration()
            .build()

}