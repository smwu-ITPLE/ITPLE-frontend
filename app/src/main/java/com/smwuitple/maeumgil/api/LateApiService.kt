package com.smwuitple.maeumgil.api

import com.smwuitple.maeumgil.dto.request.SearchLateRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface LateApiService {
    @Multipart
    @POST("/api/lates/create")
    fun createLate(
        @Header("Cookie") sessionId: String,
        @Part("data") request : RequestBody,
        @Part profile : MultipartBody.Part) : Call<ApiResponse>

    @POST("/api/lates/search")
    fun searchLate(@Body request : SearchLateRequest) : Call<ApiResponse>

    @DELETE("/api/lates/delete/{lateId}")
    fun deleteLate(@Path("lateId") lateId : String): Call<ApiResponse>
}