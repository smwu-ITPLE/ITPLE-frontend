package com.smwuitple.maeumgil.dto.request

data class KakaoReadyRequest(
    val orderId: String,
    val userId: String,
    val itemName: String,
    val totalAmount: Int,
    val vatAmount: Int,
)


data class KakaoApproveRequest(
    val orderId: String,
    val userId: String,
    val pgToken: String
)