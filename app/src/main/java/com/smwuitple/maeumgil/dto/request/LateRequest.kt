package com.smwuitple.maeumgil.dto.request

data class SearchLateRequest(
    val name : String,
    val passwd : String
)

data class ArchiveRequest(
    val nickname: String,
    val content: String
)