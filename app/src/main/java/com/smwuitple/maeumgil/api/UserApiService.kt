package com.smwuitple.maeumgil.api

import com.smwuitple.maeumgil.dto.request.*
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.dto.response.LoginResponse
import com.smwuitple.maeumgil.dto.response.ProfileResponse
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    @POST("/api/users/signup")
    fun signupUser(@Body request : SignupRequest) : Call<ApiResponse>

    @POST("/api/users/login")
    fun loginUser(@Body request : LoginRequest) : Call<LoginResponse>

    @POST("/api/users/logout")
    fun logoutUser(): Call<ApiResponse>

    @GET("/api/users/profile")
    fun profileUser(): Call<ProfileResponse>
}