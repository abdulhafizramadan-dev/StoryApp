package com.ahr.storyapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahr.storyapp.data.local.datastore.AuthPreferences
import com.ahr.storyapp.domain.Login
import com.ahr.storyapp.domain.Register
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.domain.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val storyRepository: StoryRepository,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _authToken = MutableLiveData<String>()
    val authToken: LiveData<String>
        get() = _authToken

    fun getAuthToken() {
        viewModelScope.launch {
            authPreferences.authToken.collect { token ->
                _authToken.value = token
            }
        }
    }

    fun login(email: String, password: String): LiveData<Response<Login>> {
        return storyRepository.login(email, password)
    }

    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Response<Register>> {
        return storyRepository.register(name, email, password)
    }

    fun updateAuthToken(token: String) {
        viewModelScope.launch {
            authPreferences.updateAuthToken(token)
        }
    }

    fun removeAuthToken() {
        viewModelScope.launch {
            authPreferences.removeAuthToken()
        }
    }
}