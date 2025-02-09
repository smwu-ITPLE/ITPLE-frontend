package com.smwuitple.maeumgil.api

import com.smwuitple.maeumgil.dto.request.ArchiveRequest
import com.smwuitple.maeumgil.dto.request.SearchLateRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.dto.response.EnterLateResponse
import com.smwuitple.maeumgil.dto.response.SearchLateResponse
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
        @Part profile : MultipartBody.Part ? = null) : Call<ApiResponse>

    @POST("/api/lates/search")
    fun searchLate(@Body request : SearchLateRequest) : Call<SearchLateResponse>

    @GET("/api/lates/{lateId}")
    fun enterLate(@Path("lateId") lateId : String) : Call<EnterLateResponse>

    @DELETE("/api/lates/delete/{lateId}")
    fun deleteLate(@Path("lateId") lateId : String): Call<ApiResponse>

    @POST("/api/lates/{lateId}/archive")
    fun postArchive(
        @Path("lateId") lateId: String,
        @Body request: ArchiveRequest): Call<ApiResponse>
}