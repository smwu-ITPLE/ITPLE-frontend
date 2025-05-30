// ApiService.kt 수정본
package com.smwuitple.maeumgil.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

import retrofit2.http.Body

import com.smwuitple.maeumgil.dto.response.DetectionResponse


data class TextRequest(val text: String)
data class DetectionResponse(val detected: Boolean)



interface ApiService {
    @Multipart
    @POST("/process_video")
    fun uploadVideo(
        @Part video: MultipartBody.Part,
        @Part camera: MultipartBody.Part
    ): Call<ResponseBody>

    @POST("/filter_text")
    suspend fun detectText(@Body request: TextRequest): DetectionResponse
}
