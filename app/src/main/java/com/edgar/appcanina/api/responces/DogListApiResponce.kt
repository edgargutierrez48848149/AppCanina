package com.edgar.appcanina.api.responces

import com.squareup.moshi.Json

data class DogListApiResponce (
    val message:String,
    @field:Json(name = "is_success")
    val isSuccess:Boolean,
    val data: DogListresponce
)