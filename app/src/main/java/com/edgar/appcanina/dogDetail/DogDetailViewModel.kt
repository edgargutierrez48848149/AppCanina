package com.edgar.appcanina.dogDetail

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.dogList.DogRepository
import kotlinx.coroutines.launch

class DogDetailViewModel:ViewModel() {

    private val _status = MutableLiveData<ApiResponceStatus<Any>>()
    val status: LiveData<ApiResponceStatus<Any>>
        get() = _status

    private val repository = DogRepository()

    fun addDogToUser(dogId:Long){
        viewModelScope.launch {
            _status.value = ApiResponceStatus.Loading()
            handAddDogToUserStatus(repository.addDogToUser(dogId))
        }
    }

    private fun handAddDogToUserStatus(apiResponceStatus: ApiResponceStatus<Any>) {
        _status.value = apiResponceStatus
    }

}