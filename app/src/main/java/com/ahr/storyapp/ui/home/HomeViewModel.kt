package com.ahr.storyapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.ahr.storyapp.data.local.entity.asDomain
import com.ahr.storyapp.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    fun getStories(token: String) = storyRepository.getStories(token).cachedIn(viewModelScope)
        .map { pagingData ->
            pagingData.map { story -> story.asDomain() }
        }

}