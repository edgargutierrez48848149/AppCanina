package com.edgar.appcanina.api.dto

import com.squareup.moshi.Json

data class SignUpDTO(
    val email: String,
    val password:String,
    @field:Json(name = "password_confirmation")
    val paswordConfirmation:String
)
