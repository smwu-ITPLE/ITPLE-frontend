// ApiService.kt
package com.smwuitple.maeumgil.network

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

import retrofit2.http.Body

data class TextRequest(val text: String)
data class DetectionResponse(val detected: Boolean)



interface ApiService {
    @Multipart
    @POST("/process_video")
    fun uploadVideo(
        @Part video: MultipartBody.Part
    ): Call<ResponseBody>

    @POST("/detect_text")
    suspend fun detectText(@Body request: Map<String, String>): DetectionResponse

}
