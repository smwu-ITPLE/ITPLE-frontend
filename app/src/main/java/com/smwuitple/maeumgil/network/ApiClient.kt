package com.smwuitple.maeumgil.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://54.180.149.127:5001"

    val instance: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 서버 연결 시도 시간
            .readTimeout(60, TimeUnit.SECONDS)    // 서버 응답 기다리는 시간
            .writeTimeout(60, TimeUnit.SECONDS)   // 업로드 보내는 시간
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
