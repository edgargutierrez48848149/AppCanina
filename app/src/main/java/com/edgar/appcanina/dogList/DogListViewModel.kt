package com.edgar.appcanina.dogList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.appcanina.model.Dog
import com.edgar.appcanina.api.ApiResponceStatus
import kotlinx.coroutines.launch

class DogListViewModel : ViewModel() {

    private val _dogList = MutableLiveData<List<Dog>>()
    val dogList: LiveData<List<Dog>>
        get() = _dogList

    private val _status = MutableLiveData<ApiResponceStatus<Any>>()
    val status: LiveData<ApiResponceStatus<Any>>
        get() = _status

    private val repository = DogRepository()

    init {
        getDogCollection()
    }

    private fun getDogCollection(){
        viewModelScope.launch {
            _status.value = ApiResponceStatus.Loading()
            handleResponceStatus(repository.getDogCollection())
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun handleResponceStatus(apiResponceStatus: ApiResponceStatus<List<Dog>>) {
        if (apiResponceStatus is ApiResponceStatus.Success){
            _dogList.value = apiResponceStatus.data!!
        }
        _status.value = apiResponceStatus as ApiResponceStatus<Any>
    }


}