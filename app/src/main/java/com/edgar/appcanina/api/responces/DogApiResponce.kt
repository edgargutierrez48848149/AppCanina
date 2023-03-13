package com.edgar.appcanina.api.responces

import com.edgar.appcanina.api.dto.DogDTO
import com.squareup.moshi.Json

data class DogApiResponce(
    val message:String,
    @field:Json(name = "is_success")
    val isSuccess:Boolean,
    val data: DogResponce
)
