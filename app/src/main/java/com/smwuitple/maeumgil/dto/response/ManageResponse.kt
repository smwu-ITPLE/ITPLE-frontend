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

data class ManagePayResponse(
    val data: PayData,
    val message: String,
    val status: String
)

data class PayData(
    val paySumList: List<PaySummary>,
    val payList: List<PayDetail>
)

data class PaySummary(
    val ownerName: String,
    val totalAmount: Int,
    val percentage: Double
)

data class PayDetail(
    val senderName: String,
    val receiverName: String,
    val envelope: Int,
    val amount: Int,
    val createdAt: String
)

data class ManageMessageResponse(
    val data: List<MessageDetail>?,
    val message: String,
    val status: String
)

data class MessageDetail(
    val senderName: String,
    val receiverName: String,
    val content: String?,
    val attachment: String?, // 파일 경로 (이미지 또는 비디오)
    val createdAt: String
)

data class PayResponse(
    val data: List<PayDetail>?,
    val message: String,
    val status: String
)

