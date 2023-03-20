package com.ahr.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.domain.Story
import com.ahr.storyapp.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(token: String): LiveData<Response<List<Story>>> {
        return storyRepository.getStoriesWithLatLng(token)
    }

}