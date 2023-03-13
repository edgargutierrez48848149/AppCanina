package com.edgar.appcanina.api

import okhttp3.Interceptor
import okhttp3.Response
import java.lang.RuntimeException

object ApiServiceInterceptor:Interceptor {

    const val NEEDS_AUTH_HEADER_KEY = "need_authentication"

    private var sessionToken:String? = null

    fun setSessionToken(sessionToken:String){
        this.sessionToken = sessionToken
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        if (request.header(NEEDS_AUTH_HEADER_KEY) != null){
            if (sessionToken == null){
                throw RuntimeException("Need to be authenticated t performance")
            }else{
                requestBuilder.addHeader("AUTH-TOKEN", sessionToken!!)
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}