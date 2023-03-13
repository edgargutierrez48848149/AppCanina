package com.edgar.appcanina.auth

import com.edgar.appcanina.model.User
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.api.DogsApi
import com.edgar.appcanina.api.dto.LoginDTO
import com.edgar.appcanina.api.dto.SignUpDTO
import com.edgar.appcanina.api.dto.UserDTOMapper
import com.edgar.appcanina.api.makeNetworkCall
import java.lang.Exception

class AuthRepository {

    suspend fun singUp(email: String,password:String): ApiResponceStatus<User> =
        makeNetworkCall {
            val signUpDTO = SignUpDTO(email,password,password)
            val responce = DogsApi.retrofitService.signUp(signUpDTO)
            if(!responce.isSuccess){
                throw Exception(responce.message)
            }
            val dto = responce.data.user
            val userToMapper = UserDTOMapper()
            userToMapper.fromUserDTOToUserMain(dto)
        }

    suspend fun login(email: String,password:String): ApiResponceStatus<User> =
        makeNetworkCall {
            val loginDTO = LoginDTO(email,password)
            val responce = DogsApi.retrofitService.login(loginDTO)
            if(!responce.isSuccess){
                throw Exception(responce.message)
            }
            val dto = responce.data.user
            val userToMapper = UserDTOMapper()
            userToMapper.fromUserDTOToUserMain(dto)
        }

}