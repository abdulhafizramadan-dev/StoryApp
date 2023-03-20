package com.ahr.storyapp.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.ahr.storyapp.data.local.entity.StoryEntity
import com.ahr.storyapp.domain.Login
import com.ahr.storyapp.domain.Register
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.domain.Story
import java.io.File

interface StoryRepository {

    fun register(name: String, email: String, password: String): LiveData<Response<Register>>

    fun login(email: String, password: String): LiveData<Response<Login>>

    fun getStories(token: String): LiveData<PagingData<StoryEntity>>

    fun getDetailStory(token: String, id: String): LiveData<Response<Story>>

    fun getStoriesWithLatLng(token: String): LiveData<Response<List<Story>>>

    fun addNewStory(
        token: String,
        image: File,
        description: String,
        latitude: Double? = null,
        longitude: Double? = null
    ): LiveData<Response<String>>
}