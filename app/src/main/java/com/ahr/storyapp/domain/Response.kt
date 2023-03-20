package com.ahr.storyapp.domain

sealed interface Response<out R> {
    object Loading : Response<Nothing>
    object Empty : Response<Nothing>
    data class Success<T>(val data: T) : Response<T>
    data class Error(val message: String?) : Response<Nothing>
}