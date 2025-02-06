package com.smwuitple.maeumgil.api

import com.smwuitple.maeumgil.dto.response.ShareResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ManageApiService {
    @GET("/api/manage/{lateId}/share")
    fun shareLate(@Header("Cookie") sessionId: String,
                  @Path("lateId") lateId : String) : Call<ShareResponse>


}