package com.ahr.storyapp.data.network.response

import com.ahr.storyapp.domain.Register
import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

fun RegisterResponse.asDomain(): Register =
	Register(
		error = error ?: false,
		message = message ?: ""
	)
