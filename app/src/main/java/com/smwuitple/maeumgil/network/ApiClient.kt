package com.smwuitple.maeumgil.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



object ApiClient {
    private const val BASE_URL_VIDEO = "http://54.180.149.127:5001"  // 비디오 서버용
    private const val BASE_URL_TEXT = "http://54.180.149.127:5002"   // 텍스트 필터링 서버용

    // 비디오 기능용 API
    val videoApiService: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL_VIDEO)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // 텍스트 필터링용 API
    val textApiService: ApiService by lazy {
        val client = OkHttpClient.Builder().build()

        Retrofit.Builder()
            .baseUrl(BASE_URL_TEXT)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
