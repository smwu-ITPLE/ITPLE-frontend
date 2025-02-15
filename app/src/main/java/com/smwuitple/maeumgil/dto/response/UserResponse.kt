package com.smwuitple.maeumgil.dto.response

data class LoginResponse(
    val data: UserData?,
    val message: String,
    val status: String
)

data class UserData(
    val phonenumber: String,
    val name: String,
    val id: Int
)

data class ProfileResponse(
    val data: UserData2?,
    val message: String,
    val status: String
)

data class UserData2(
    val name: String
)