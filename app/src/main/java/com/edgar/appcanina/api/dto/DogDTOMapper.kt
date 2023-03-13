package com.edgar.appcanina.api.dto

import com.edgar.appcanina.model.Dog

class DogDTOMapper {
    fun fromDogDTOToDogMain(dogDto: DogDTO): Dog {
        return Dog(
            dogDto.id,
            dogDto.index,
            dogDto.name,
            dogDto.type,
            dogDto.heightFemale,
            dogDto.heightMale,
            dogDto.imageUrl,
            dogDto.lifeExpectancy,
            dogDto.temperament,
            dogDto.weightFemale,
            dogDto.weightMale
        )
    }

    fun fromDogDTOListToDogMain(dogDtoList:List<DogDTO>):List<Dog>{
        return dogDtoList.map { fromDogDTOToDogMain(it) }
    }
}