package com.edgar.appcanina.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.lang.Exception

private const val UNAUTORIZED_CODE = 401
private const val BAD_REQUEST = 400

suspend fun <T> makeNetworkCall(
    call: suspend () -> T
): ApiResponceStatus<T> = withContext(Dispatchers.IO){
    try {
        ApiResponceStatus.Success(call())
    }catch (e:HttpException){
        val error = when(e.code()){
            BAD_REQUEST -> e.message()
            UNAUTORIZED_CODE -> "Usuario o contrasena invalida"
            else -> e.message()
        }
        ApiResponceStatus.Error(error)
    }catch (e:Exception){
        ApiResponceStatus.Error(e.message.toString())
    }
}