package com.ahr.storyapp.di

import com.ahr.storyapp.BuildConfig
import com.ahr.storyapp.data.network.service.StoryConfig
import com.ahr.storyapp.data.network.service.StoryService
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideStoryService(okHttpClient: OkHttpClient): StoryService {
        return Retrofit.Builder()
            .baseUrl(StoryConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(StoryService::class.java)
    }

    @Provides
    @Singleton
    fun okHttpLoggingInterceptor(): OkHttpClient {
        val level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
        val logging = HttpLoggingInterceptor().setLevel(level)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }
}