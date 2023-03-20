package com.ahr.storyapp.data.network.response

import com.ahr.storyapp.domain.Login
import com.google.gson.annotations.SerializedName

data class LoginResult(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)

fun LoginResult.asDomain(): Login =
	Login(
		name = name ?: "",
		userId = userId ?: "",
		token = token ?: ""
	)
