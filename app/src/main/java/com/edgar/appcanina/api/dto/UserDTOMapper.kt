package com.edgar.appcanina.api.dto

import com.edgar.appcanina.model.User

class UserDTOMapper {

    fun fromUserDTOToUserMain(userDTO: UserDTO): User = User(
        userDTO.id,
        userDTO.email,
        userDTO.autenticationToken
    )
}