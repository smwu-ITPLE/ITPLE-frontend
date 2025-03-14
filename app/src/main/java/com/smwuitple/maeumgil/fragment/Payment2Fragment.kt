package com.smwuitple.maeumgil.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.api.LateApiService
import com.smwuitple.maeumgil.api.PaymentApiService
import com.smwuitple.maeumgil.dto.request.KakaoApproveRequest
import com.smwuitple.maeumgil.dto.request.KakaoReadyRequest
import com.smwuitple.maeumgil.dto.request.PaymentRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.dto.response.KakaoApproveResponse
import com.smwuitple.maeumgil.dto.response.KakaoReadyResponse
import com.smwuitple.maeumgil.PaymentWebViewActivity
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Payment2Fragment : Fragment() {

    private var lateId: String? = null  // 조문공간 ID 추가
    private var ownerName: String? = null
    private var ownerId: Int? = null
    private var amount: Int? = null
    private var selectedEnvelope: Int = 1 // 기본 봉투 설정 (1번 선택)
    private var orderId: String = "ORDER_" + System.currentTimeMillis() // 고유 주문 ID 생성

    private lateinit var checkboxCard1: CheckBox
    private lateinit var checkboxCard2: CheckBox
    private lateinit var cardOption1: RelativeLayout
    private lateinit var cardOption2: RelativeLayout

    private lateinit var paymentApiService: PaymentApiService
    private lateinit var apiService2: LateApiService

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lateId = it.getString("late_id") // 조문공간 ID 추가
            ownerName = it.getString("owner_name")
            ownerId = it.getInt("owner_id", -1)
            amount = it.getInt("amount", 0)
        }

        paymentApiService = RetrofitClient.getPaymentApi(requireContext())
        apiService2 = RetrofitClient.getLateApi(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment2, container, false)

        val receiverTextView = view.findViewById<TextView>(R.id.txt_receiver)
        val amountTextView = view.findViewById<TextView>(R.id.txt_amount)
        val sendButton = view.findViewById<Button>(R.id.manage_button)
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        val cancelButton = view.findViewById<Button>(R.id.home_button)


        // 카드 체크박스 및 레이아웃 바인딩
        checkboxCard1 = view.findViewById(R.id.checkbox_card_1)
        checkboxCard2 = view.findViewById(R.id.checkbox_card_2)
        cardOption1 = view.findViewById(R.id.card_option_1)
        cardOption2 = view.findViewById(R.id.card_option_2)

        // 체크박스를 클릭할 수 없도록 설정 (카드를 눌러야 선택됨)
        checkboxCard1.isClickable = false
        checkboxCard2.isClickable = false

        // 수신자 및 금액 표시
        receiverTextView.text = "$ownerName 님"
        amountTextView.text = "${formatNumber(amount ?: 0)}원"

        // 기본 선택값 설정 (첫 번째 카드 선택)
        updateSelectedEnvelope(1)

        // 카드 클릭 시 단일 선택 구현
        cardOption1.setOnClickListener {
            updateSelectedEnvelope(1)
        }

        cardOption2.setOnClickListener {
            updateSelectedEnvelope(2)
        }

        // 뒤로 가기 버튼
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 취소 버튼 (홈으로 이동 또는 창 닫기)
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 송금 버튼 클릭 → API 호출
        sendButton.setOnClickListener {
            startKakaoPay()
        }

        return view
    }

    // 봉투 선택 상태 업데이트 (항상 하나만 선택됨)
    private fun updateSelectedEnvelope(envelope: Int) {
        selectedEnvelope = envelope
        checkboxCard1.isChecked = (envelope == 1)
        checkboxCard2.isChecked = (envelope == 2)

        // 선택된 카드 강조 (배경색 변경 가능)
        cardOption1.setBackgroundResource(if (envelope == 1) R.drawable.selected_card_background else R.drawable.default_card_background)
        cardOption2.setBackgroundResource(if (envelope == 2) R.drawable.selected_card_background else R.drawable.default_card_background)
    }

    //카카오페이 ready 요청
    private fun startKakaoPay() {
        if (lateId.isNullOrEmpty() || ownerId == null || amount == null) {
            Toast.makeText(requireContext(), "잘못된 결제 정보입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val paymentRequest = KakaoReadyRequest(
            orderId = this.orderId,
            userId = ownerId.toString(),
            itemName = "부의금",
            totalAmount = amount ?: 0,
            vatAmount = (amount ?: 0) / 10,
        )

        paymentApiService.kakaopayReady(paymentRequest).enqueue(object : Callback<KakaoReadyResponse> {
            override fun onResponse(call: Call<KakaoReadyResponse>, response: Response<KakaoReadyResponse>) {
                val kakaoReadyResponse = response.body()

                if (response.isSuccessful && kakaoReadyResponse != null) {
                    val redirectUrl = kakaoReadyResponse.data?.next_redirect_pc_url
                    Log.d("Payment2Fragment", "Redirect PC URL: $redirectUrl")

                    if (!redirectUrl.isNullOrEmpty()) {
                        openWebView(redirectUrl) // WebView 띄우기
                    } else {
                        Toast.makeText(requireContext(), "결제 준비 실패: Redirect URL 없음", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("Payment2Fragment", "Response 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "결제 준비 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<KakaoReadyResponse>, t: Throwable) {
                Log.e("Payment2Fragment", "API 호출 실패: ${t.message}")
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openWebView(url: String) {
        val intent = Intent(requireContext(), PaymentWebViewActivity::class.java)
        intent.putExtra("payment_url", url)
        intent.putExtra("order_id", orderId)
        intent.putExtra("user_id", ownerId.toString())
        Log.d("Payment2", "🔍 Received Payment URL: $url")
        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK) {
            val pgToken = data?.getStringExtra("pg_token")

            if (!pgToken.isNullOrEmpty()) {
                Log.d("Payment2Fragment", "✅ Received pg_token in Fragment: $pgToken")
                approveKakaoPay(pgToken)
            } else {
                Log.e("Payment2Fragment", "pg_token is null")
                Toast.makeText(requireContext(), "결제 승인 실패 (pg_token 없음)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 카카오페이 결제 승인
    fun approveKakaoPay(pgToken: String) {
        val request = KakaoApproveRequest(
            orderId = this.orderId,
            userId = ownerId.toString(),
            pgToken = pgToken
        )

        paymentApiService.kakaopayApprove(request).enqueue(object : Callback<KakaoApproveResponse> {
            override fun onResponse(call: Call<KakaoApproveResponse>, response: Response<KakaoApproveResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        sendPayment() // 결제 승인 후 송금 API 호출
                    }
                } else {
                    Toast.makeText(requireContext(), "결제 승인 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<KakaoApproveResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendPayment() {
        // apiService2가 초기화되지 않았을 경우 방어 코드 추가
        if (!::apiService2.isInitialized) {
            Log.e("Payment2Fragment", "apiService2 is not initialized!")
            return
        }

        val paymentRequest = PaymentRequest(
            receiverId = ownerId ?: -1,
            envelope = selectedEnvelope,
            amount = amount ?: 0
        )

        apiService2.sendPayment(lateId!!, paymentRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "부의금 전송 성공!", Toast.LENGTH_SHORT).show()

                    PaymentSuccessFragment.newInstance().show(parentFragmentManager, "PaymentSuccessFragment")
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment.newInstance())
                        .commit()

                } else {
                    Toast.makeText(requireContext(), "송금 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 숫자를 3자리마다 콤마(,)를 붙이는 함수
    private fun formatNumber(number: Int): String {
        return String.format("%,d", number)
    }

    companion object {
        private const val REQUEST_CODE_PAYMENT = 1001
    }
}
