package com.smwuitple.maeumgil.dto.response

data class KakaoReadyResponse(
    val data: KakaoReadyData?,
    val message: String,
    val status: String
)

data class KakaoReadyData(
    val tid: String,
    val next_redirect_app_url: String,
    val next_redirect_mobile_url: String,
    val next_redirect_pc_url: String,
    val android_app_scheme: String?,
    val ios_app_scheme: String?,
    val created_at: String
)
data class KakaoApproveResponse(
    val data: KakaoApproveData?,
    val message: String,
    val status: String
)

data class KakaoApproveData(
    val aid: String,
    val tid: String,
    val cid: String,
    val sid: String?,
    val partner_order_id: String,
    val partner_user_id: String,
    val payment_method_type: String,
    val amount: Amount,
    val item_name: String,
    val item_code: String?,
    val quantity: Int,
    val created_at: String,
    val approved_at: String,
    val payload: String?
)

data class Amount(
    val total: Int,
    val tax_free: Int,
    val vat: Int,
    val point: Int,
    val discount: Int
)
