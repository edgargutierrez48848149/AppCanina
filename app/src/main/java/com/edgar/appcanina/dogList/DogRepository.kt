package com.edgar.appcanina.dogList

import com.edgar.appcanina.model.Dog
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.api.DogsApi.retrofitService
import com.edgar.appcanina.api.dto.AddDogToUserDTO
import com.edgar.appcanina.api.dto.DogDTOMapper
import com.edgar.appcanina.api.makeNetworkCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.lang.StringBuilder

class DogRepository {

    suspend fun getDogCollection(): ApiResponceStatus<List<Dog>> = withContext(Dispatchers.IO) {
        val allDogsListDeferred = async { downloadDogs() }
        val userDogsDeferred = async { getUserDogs() }

        val allDogsListResponse = allDogsListDeferred.await()
        val userDogsResponce = userDogsDeferred.await()

        if (allDogsListResponse is ApiResponceStatus.Error) {
            allDogsListResponse
        } else if (userDogsResponce is ApiResponceStatus.Error) {
            userDogsResponce
        } else if (allDogsListResponse is ApiResponceStatus.Success &&
            userDogsResponce is ApiResponceStatus.Success
        ) {
            ApiResponceStatus.Success(
                getCollectionList(
                    allDogsListResponse.data,
                    userDogsResponce.data
                )
            )
        } else {
            ApiResponceStatus.Error("Error desconocido")
        }
    }


    private fun getCollectionList(allDogList: List<Dog>, userDogList: List<Dog>): List<Dog> =
        allDogList.map {
            if (userDogList.contains(it)) {
                it
            } else {
                Dog(
                    0, it.index, "", "", "", "", "",
                    "", "", "", "",inCollection = false
                )
            }
        }.sorted()

    private suspend fun downloadDogs(): ApiResponceStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponce = retrofitService.getAllDogs()
        val dogDTOList = dogListApiResponce.data.dogs
        val dogToMapper = DogDTOMapper()
        dogToMapper.fromDogDTOListToDogMain(dogDTOList)
    }

    private suspend fun getUserDogs(): ApiResponceStatus<List<Dog>> = makeNetworkCall {
        val dogListApiResponce = retrofitService.getUserDogs()
        val dogDTOList = dogListApiResponce.data.dogs
        val dogToMapper = DogDTOMapper()
        dogToMapper.fromDogDTOListToDogMain(dogDTOList)
    }

    suspend fun addDogToUser(dogId: Long): ApiResponceStatus<Any> = makeNetworkCall {
        val addDogToUserDTO = AddDogToUserDTO(dogId)
        val defaultResponce = retrofitService.addDogUser(addDogToUserDTO)
        if (!defaultResponce.isSuccess) {
            throw Exception(defaultResponce.message)
        }
    }

    suspend fun getRecognizedDog(mlDogId:String): ApiResponceStatus<Dog> = makeNetworkCall {
        val response = retrofitService.getDogByMlid(mlDogId)
        if (!response.isSuccess){
            throw Exception(response.message)
        }
        val dogDTOMapper = DogDTOMapper()
        dogDTOMapper.fromDogDTOToDogMain(response.data.dog)
    }
}
