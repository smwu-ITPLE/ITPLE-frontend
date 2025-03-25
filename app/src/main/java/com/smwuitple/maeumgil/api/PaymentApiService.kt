package com.smwuitple.maeumgil.api

import com.smwuitple.maeumgil.dto.request.KakaoApproveRequest
import com.smwuitple.maeumgil.dto.request.KakaoReadyRequest
import com.smwuitple.maeumgil.dto.response.KakaoApproveResponse
import com.smwuitple.maeumgil.dto.response.KakaoReadyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApiService {
    // 결제 준비 요청
    @POST("/api/payment/ready")
    fun kakaopayReady(@Body request: KakaoReadyRequest): Call<KakaoReadyResponse>

    // 결제 승인 요청
    @POST("/api/payment/success")
    fun kakaopayApprove(@Body request: KakaoApproveRequest): Call<KakaoApproveResponse>
}