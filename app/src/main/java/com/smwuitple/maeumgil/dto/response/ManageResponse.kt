package com.smwuitple.maeumgil.dto.response


data class ShareResponse(
    val data: ShareLateData?,
    val message: String,
    val status: String
)


data class ShareLateData(
    val name: String,
    val passwd: String,
    val content: String,
    val userName : String,
    val userPhonenumber: String
)

data class LateListItem(
    val id: Int,
    val name: String
)