package com.ahr.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.ahr.storyapp.data.local.database.StoryDatabase
import com.ahr.storyapp.data.local.entity.StoryEntity
import com.ahr.storyapp.data.network.response.asDomain
import com.ahr.storyapp.data.network.service.StoryService
import com.ahr.storyapp.domain.Login
import com.ahr.storyapp.domain.Register
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.domain.Story
import com.ahr.storyapp.domain.repository.StoryRepository
import com.ahr.storyapp.helper.wrapEspressoIdlingResource
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepositoryImpl @Inject constructor(
    private val storyDatabase: StoryDatabase,
    private val storyService: StoryService
) : StoryRepository {

    override fun register(name: String, email: String, password: String): LiveData<Response<Register>> = liveData {
        emit(Response.Loading)
        wrapEspressoIdlingResource {
            when (val response = storyService.register(name, email, password)) {
                is NetworkResponse.Success -> {
                    emit(Response.Success(response.body.asDomain()))
                }
                is NetworkResponse.Error -> {
                    emit(Response.Error(response.body?.message))
                }
            }
        }
    }

    override fun login(email: String, password: String): LiveData<Response<Login>> = liveData {
        emit(Response.Loading)
        wrapEspressoIdlingResource {
            when (val response = storyService.login(email, password)) {
                is NetworkResponse.Success -> {
                    if (response.body.loginResult != null) {
                        emit(Response.Success(response.body.loginResult!!.asDomain()))
                    }
                }
                is NetworkResponse.Error -> {
                    emit(Response.Error(response.body?.message))
                }
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getStories(token: String): LiveData<PagingData<StoryEntity>> =
        wrapEspressoIdlingResource {
            Pager(
                config = PagingConfig(pageSize = 50),
                remoteMediator = StoryRemoteMediator(storyDatabase, storyService, token),
                pagingSourceFactory = { storyDatabase.storyDao().getStories() }
            ).liveData
        }

    override fun getDetailStory(token: String, id: String): LiveData<Response<Story>> = liveData {
        emit(Response.Loading)
        wrapEspressoIdlingResource {
            when (val response = storyService.getDetailStory(token, id)) {
                is NetworkResponse.Success -> {
                    if (response.body.story != null) {
                        emit(Response.Success(response.body.story!!.asDomain()))
                    } else {
                        emit(Response.Empty)
                    }
                }
                is NetworkResponse.Error -> {
                    emit(Response.Error(response.body?.message))
                }
            }
        }
    }

    override fun getStoriesWithLatLng(token: String): LiveData<Response<List<Story>>> = liveData {
        emit(Response.Loading)
        wrapEspressoIdlingResource {
            when (val response = storyService.getStories(token = token, size = 30, location = 1)) {
                is NetworkResponse.Success -> {
                    if (response.body.listStory != null) {
                        emit(Response.Success(response.body.listStory!!.asDomain()))
                    } else {
                        emit(Response.Empty)
                    }
                }
                is NetworkResponse.Error -> {
                    emit(Response.Error(response.body?.message))
                }
            }
        }
    }

    override fun addNewStory(
        token: String,
        image: File,
        description: String,
        latitude: Double?,
        longitude: Double?
    ): LiveData<Response<String>> = liveData {
        emit(Response.Loading)
        wrapEspressoIdlingResource {
            try {
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
                val imageRequestBody = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    image.name,
                    imageRequestBody
                )

                val response = if (latitude != null && longitude != null) {
                    val latitudeRequestBody = latitude.toString().toRequestBody("text/plain".toMediaType())
                    val longitudeRequestBody = longitude.toString().toRequestBody("text/plain".toMediaType())
                    storyService.addNewStoryWithLocation(
                        token,
                        imageMultiPart,
                        descriptionRequestBody,
                        latitudeRequestBody,
                        longitudeRequestBody
                    )
                } else {
                    storyService.addNewStory(
                        token,
                        imageMultiPart,
                        descriptionRequestBody
                    )
                }

                when (response) {
                    is NetworkResponse.Success -> {
                        emit(Response.Success(response.body.message))
                    }
                    is NetworkResponse.Error -> {
                        emit(Response.Error(response.body?.message))
                    }
                }
            } catch (exception: Exception) {
                emit(Response.Error(exception.message))
            }
        }
    }
}