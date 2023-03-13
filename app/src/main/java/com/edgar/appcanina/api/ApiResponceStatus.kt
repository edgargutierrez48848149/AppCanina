package com.edgar.appcanina.api

sealed class ApiResponceStatus<T> {
    class Success<T>(val data: T): ApiResponceStatus<T>()
    class Loading<T>: ApiResponceStatus<T>()
    class Error<T>(val message: String): ApiResponceStatus<T>()
}