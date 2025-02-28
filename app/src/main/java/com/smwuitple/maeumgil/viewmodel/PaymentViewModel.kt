package com.smwuitple.maeumgil.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.smwuitple.maeumgil.api.PaymentApiService
import com.smwuitple.maeumgil.dto.request.KakaoApproveRequest
import com.smwuitple.maeumgil.dto.request.KakaoReadyRequest
import com.smwuitple.maeumgil.dto.response.KakaoApproveResponse
import com.smwuitple.maeumgil.dto.response.KakaoReadyResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class PaymentViewModel(private val context: Context) : ViewModel() {
    private val paymentApi: PaymentApiService = RetrofitClient.getPaymentApi(context)

    // 카카오페이 결제 준비 요청 (Ready)
    fun requestKakaoPayReady(
        request: KakaoReadyRequest,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        paymentApi.kakaopayReady(request).enqueue(object : Callback<KakaoReadyResponse> {
            override fun onResponse(call: Call<KakaoReadyResponse>, response: Response<KakaoReadyResponse>) {
                if (response.isSuccessful) {
                    val kakaoReadyResponse = response.body()

                    // JSON에서 "data" 필드 내부 값을 가져오도록 수정
                    val redirectUrl = kakaoReadyResponse?.data?.next_redirect_pc_url

                    if (!redirectUrl.isNullOrEmpty()) {
                        onSuccess(redirectUrl) // 결제 페이지 URL 반환
                    } else {
                        onError("결제 준비 실패: Redirect URL 없음")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                    Log.e("PaymentViewModel", "결제 준비 실패: $errorMessage")
                    onError("결제 준비 실패: $errorMessage")
                }
            }

            override fun onFailure(call: Call<KakaoReadyResponse>, t: Throwable) {
                Log.e("PaymentViewModel", "결제 준비 요청 실패: ${t.message}")
                onError("결제 준비 요청 실패: ${t.message}")
            }
        })
    }

    // 카카오페이 결제 승인 요청 (Approve)
    fun requestKakaoPayApprove(
        request: KakaoApproveRequest,
        onSuccess: (KakaoApproveResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        paymentApi.kakaopayApprove(request).enqueue(object : Callback<KakaoApproveResponse> {
            override fun onResponse(call: Call<KakaoApproveResponse>, response: Response<KakaoApproveResponse>) {
                if (response.isSuccessful) {
                    val kakaoApproveResponse = response.body()

                    Log.d("PaymentViewModel", "결제 승인 응답: $kakaoApproveResponse")

                    if (kakaoApproveResponse != null) {
                        onSuccess(kakaoApproveResponse) // 승인 응답 반환
                    } else {
                        onError("응답 데이터 없음")
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "알 수 없는 오류"
                    Log.e("PaymentViewModel", "결제 승인 실패: $errorMessage")

                    // 카카오 API 오류 코드 확인
                    val errorResponse = response.errorBody()?.string()
                    Log.e("PaymentViewModel", "서버에서 반환된 에러 메시지: $errorResponse")

                    onError("결제 승인 실패: $errorMessage")
                }
            }

            override fun onFailure(call: Call<KakaoApproveResponse>, t: Throwable) {
                Log.e("PaymentViewModel", "결제 승인 요청 실패: ${t.message}")
                onError("결제 승인 요청 실패: ${t.message}")
            }
        })
    }
}
