package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.request.PaymentRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
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

    private lateinit var checkboxCard1: CheckBox
    private lateinit var checkboxCard2: CheckBox
    private lateinit var cardOption1: RelativeLayout
    private lateinit var cardOption2: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lateId = it.getString("late_id") // 조문공간 ID 추가
            ownerName = it.getString("owner_name")
            ownerId = it.getInt("owner_id", -1)
            amount = it.getInt("amount", 0)
        }

        Log.d("Payment2Fragment", "onCreate - lateId: $lateId, ownerId: $ownerId, amount: $amount")
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
            sendPayment()
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

    private fun sendPayment() {

        if (lateId.isNullOrEmpty() || ownerId == null || ownerId == -1 || amount == null || amount == 0) {
            Toast.makeText(requireContext(), "잘못된 송금 정보입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.getLateApi(requireContext())

        val paymentRequest = PaymentRequest(
            receiverId = ownerId ?: -1,
            envelope = selectedEnvelope,
            amount = amount ?: 0
        )

        apiService.sendPayment(lateId!!, paymentRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "부의금 전송 성공!", Toast.LENGTH_SHORT).show()

                    val privateMainFragment = PrivateMainFragment.newInstance(lateId ?: "")
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, privateMainFragment)
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
}
