package com.smwuitple.maeumgil.api

import com.smwuitple.maeumgil.dto.response.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ManageApiService {
    @GET("/api/manage/{lateId}/share")
    fun shareLate(@Path("lateId") lateId : String) : Call<ShareResponse>

    @GET("/api/manage/latelist")
    fun getLateList(): Call<ApiResponse>

    @GET("/api/manage/{lateId}/pay")
    fun getPayList(@Path("lateId") lateId : String) : Call<ManagePayResponse>

    @GET("/api/manage/{lateId}/message")
    fun getMessageList(@Path("lateId") lateId : String) : Call<ManageMessageResponse>

    @GET("/api/manage/message")
    fun getMessages():Call<ManageMessageResponse>

    @GET("/api/manage/pay")
    fun getPayments(): Call<PayResponse>
}