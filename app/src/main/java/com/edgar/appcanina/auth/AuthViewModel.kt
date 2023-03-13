package com.edgar.appcanina.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.model.User
import kotlinx.coroutines.launch

class AuthViewModel:ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _status = MutableLiveData<ApiResponceStatus<User>>()
    val status: LiveData<ApiResponceStatus<User>>
        get() = _status

    private val repository = AuthRepository()

    fun signUp(email: String,password:String){
        viewModelScope.launch {
            _status.value = ApiResponceStatus.Loading()
            handleResponceStatus(repository.singUp(email,password))
        }
    }

    private fun handleResponceStatus(apiResponceStatus: ApiResponceStatus<User>){
        if (apiResponceStatus is ApiResponceStatus.Success){
            _user.value = apiResponceStatus.data!!
        }
        _status.value = apiResponceStatus
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _status.value = ApiResponceStatus.Loading()
            handleResponceStatus(repository.login(email,password))
        }
    }
}