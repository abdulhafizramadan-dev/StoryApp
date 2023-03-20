package com.ahr.storyapp.data.network.service

import com.ahr.storyapp.data.network.response.*
import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StoryService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : NetworkResponse<LoginResponse, CommonNetworkErrorResponse>

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : NetworkResponse<RegisterResponse, CommonNetworkErrorResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 0
    ) : NetworkResponse<StoryResponse, CommonNetworkErrorResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ) : NetworkResponse<DetailStoryResponse, CommonNetworkErrorResponse>

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ) : NetworkResponse<AddNewStoryResponse, CommonNetworkErrorResponse>

    @Multipart
    @POST("stories")
    suspend fun addNewStoryWithLocation(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitude: RequestBody? = null,
        @Part("lon") longitude: RequestBody? = null,
    ) : NetworkResponse<AddNewStoryResponse, CommonNetworkErrorResponse>
}