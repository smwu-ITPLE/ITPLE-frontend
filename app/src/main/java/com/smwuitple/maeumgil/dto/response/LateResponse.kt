package com.smwuitple.maeumgil.dto.response

data class SearchLateResponse(
    val data : Int,
    val message: String,
    val status: String
)

data class EnterLateResponse(
    val data: EnterLateData?,
    val message: String,
    val status: String
)

data class EnterLateData(
    val name: String,
    val profile: String,
    val age: Int,
    val gender: String,
    val datePass: String,
    val dateDeath: String,
    val location: String,
    val owners: List<Owner>,
    val archives: List<Archive>
)

data class Owner(
    val name: String,
    val relation: String,
    val phoneNumber: String
)

data class Archive(
    val nickname: String,
    val content: String
)
