package com.edgar.appcanina.api

import com.edgar.appcanina.BASE_URL
import com.edgar.appcanina.api.dto.AddDogToUserDTO
import com.edgar.appcanina.api.dto.LoginDTO
import com.edgar.appcanina.api.dto.SignUpDTO
import com.edgar.appcanina.api.responces.DogListApiResponce
import com.edgar.appcanina.api.responces.AuthApiResponce
import com.edgar.appcanina.api.responces.DefaultResponse
import com.edgar.appcanina.api.responces.DogApiResponce
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private val okHttpClient = OkHttpClient
    .Builder()
    .addInterceptor(ApiServiceInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface ApiService{
    @GET("dogs")
    suspend fun getAllDogs(): DogListApiResponce

    @POST("sign_up")
    suspend fun signUp(@Body signUpDTO: SignUpDTO): AuthApiResponce

    @POST("sign_in")
    suspend fun login(@Body loginDTO: LoginDTO): AuthApiResponce

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}: true")
    @POST("add_dog_to_user")
    suspend fun addDogUser(@Body addDogToUserDTO: AddDogToUserDTO): DefaultResponse

    @Headers("${ApiServiceInterceptor.NEEDS_AUTH_HEADER_KEY}: true")
    @GET("get_user_dogs")
    suspend fun getUserDogs():DogListApiResponce

    @GET("find_dog_by_ml_id")
    suspend fun getDogByMlid(@Query("ml_id")mlId:String): DogApiResponce
}

object DogsApi{
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}