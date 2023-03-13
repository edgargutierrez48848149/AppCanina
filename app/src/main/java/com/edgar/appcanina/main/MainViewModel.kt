package com.edgar.appcanina.main

import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.dogList.DogRepository
import com.edgar.appcanina.machineLerning.ClasifierRepository
import com.edgar.appcanina.machineLerning.Classifier
import com.edgar.appcanina.machineLerning.DogRecognition
import com.edgar.appcanina.model.Dog
import kotlinx.coroutines.launch
import java.nio.MappedByteBuffer

class MainViewModel : ViewModel() {

    private val _dog = MutableLiveData<Dog>()
    val dog: LiveData<Dog>
        get() = _dog


    private val _status = MutableLiveData<ApiResponceStatus<Dog>>()
    val status: LiveData<ApiResponceStatus<Dog>>
        get() = _status

    private val _dogRecognition = MutableLiveData<DogRecognition>()
    val dogRecognition: LiveData<DogRecognition>
        get() = _dogRecognition

    private val dogRepository = DogRepository()
    private lateinit var classifierRepository: ClasifierRepository

    fun setupClassifier(tfLiteModel: MappedByteBuffer, labels: List<String>){
        val classifier = Classifier(tfLiteModel,labels)
        classifierRepository = ClasifierRepository(classifier)
    }

    fun recognizeImage(imageProxy: ImageProxy){
        viewModelScope.launch {
            _dogRecognition.value = classifierRepository.recognizerImage(imageProxy)
            imageProxy.close()
        }
    }

    fun getRecognizedDog(mlDogId: String) {
        viewModelScope.launch {
            handleResponceSatus(dogRepository.getRecognizedDog(mlDogId))
        }
    }

    private fun handleResponceSatus(apiResponceStatus: ApiResponceStatus<Dog>){
        if (apiResponceStatus is ApiResponceStatus.Success){
            _dog.value = apiResponceStatus.data!!
        }
        _status.value = apiResponceStatus
    }
}