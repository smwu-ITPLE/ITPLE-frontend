package com.smwuitple.maeumgil.utils

import com.smwuitple.maeumgil.api.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.219.107:8081"
    // res/xml/network_security_config.xml도 변경

    // 쿠키 저장 변수
    private var sessionCookie: String? = null

    // 쿠키 저장 Interceptor
    private val cookieInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 기존 쿠키가 있으면 헤더에 추가
        sessionCookie?.let {
            requestBuilder.addHeader("Cookie", it)
        }

        val response = chain.proceed(requestBuilder.build())

        // 응답에서 JSESSIONID 쿠키 저장
        response.headers("Set-Cookie").forEach { cookie ->
            if (cookie.startsWith("JSESSIONID")) {
                sessionCookie = cookie
            }
        }

        response
    }

    // OkHttp 로그 인터셉터 추가 (네트워크 요청 로그 출력)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // User API 인스턴스
    val userApi: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }

    // Late API 인스턴스
    val lateApi: LateApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LateApiService::class.java)
    }

    // Manage API 인스턴스
    val manageApi: ManageApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ManageApiService::class.java)
    }
}