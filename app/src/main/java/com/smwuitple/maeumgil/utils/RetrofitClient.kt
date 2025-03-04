package com.smwuitple.maeumgil.utils

import android.content.Context
import com.smwuitple.maeumgil.api.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.219.109:8081" //"http://13.125.216.241:8081"

    // Retrofit 인스턴스를 싱글톤으로 유지
    @Volatile
    private var retrofitInstance: Retrofit? = null

    private fun createRetrofit(context: Context): Retrofit {
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

    // Retrofit 싱글톤 인스턴스 반환
    private fun getRetrofitInstance(context: Context): Retrofit {
        return retrofitInstance ?: synchronized(this) {
            retrofitInstance ?: createRetrofit(context).also { retrofitInstance = it }
        }
    }

    // API 서비스 반환 (함수 개선)
    fun getUserApi(context: Context): UserApiService = getRetrofitInstance(context).create(UserApiService::class.java)
    fun getLateApi(context: Context): LateApiService {
        if (retrofitInstance == null) {
            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // 서버 연결 대기 시간 (기본값: 10초)
                .readTimeout(60, TimeUnit.SECONDS)     // 서버 응답 대기 시간 (기본값: 10초)
                .writeTimeout(60, TimeUnit.SECONDS)    // 요청 데이터 전송 대기 시간 (기본값: 10초)
                .build()

            retrofitInstance = Retrofit.Builder()
                .baseUrl("https://your-api-url.com/") // 여기에 실제 API 주소 입력
                .client(client) // 👈 OkHttpClient 적용
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofitInstance!!.create(LateApiService::class.java)
    }
    fun getManageApi(context: Context): ManageApiService = getRetrofitInstance(context).create(ManageApiService::class.java)
    fun getPaymentApi(context: Context): PaymentApiService = getRetrofitInstance(context).create(PaymentApiService::class.java)
}
