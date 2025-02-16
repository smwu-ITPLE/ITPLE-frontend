package com.smwuitple.maeumgil.utils

import android.content.Context
import com.smwuitple.maeumgil.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.219.111:8081"

    fun getClient(context: Context): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .cookieJar(CustomCookieJar(context))  // 쿠키 자동 저장 & 불러오기 추가
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // User API 인스턴스
    fun getUserApi(context: Context): UserApiService {
        return getClient(context).create(UserApiService::class.java)
    }

    // Late API 인스턴스
    fun getLateApi(context: Context): LateApiService {
        return getClient(context).create(LateApiService::class.java)
    }

    // Manage API 인스턴스
    fun getManageApi(context: Context): ManageApiService {
        return getClient(context).create(ManageApiService::class.java)
    }
}
