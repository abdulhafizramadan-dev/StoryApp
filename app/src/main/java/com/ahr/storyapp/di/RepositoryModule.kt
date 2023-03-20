package com.ahr.storyapp.di

import com.ahr.storyapp.data.StoryRepositoryImpl
import com.ahr.storyapp.domain.repository.StoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideStoryRepository(
        storyRepositoryImpl: StoryRepositoryImpl
    ) : StoryRepository
}