package com.smwuitple.maeumgil.dto.request

data class SignupRequest(
    val name: String,
    val phonenumber: String,
    val passwd: String
)

data class LoginRequest (
    val phonenumber: String,
    val passwd: String
)