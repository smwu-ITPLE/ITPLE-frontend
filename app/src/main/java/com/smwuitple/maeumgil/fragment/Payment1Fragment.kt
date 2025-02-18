package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.smwuitple.maeumgil.R

class Payment1Fragment : Fragment() {

    private var lateId: String? = null
    private var ownerName: String? = null
    private var ownerId: Int? = null
    private var amount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lateId = it.getString("late_id")
            ownerName = it.getString("owner_name")
            ownerId = it.getInt("owner_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment1, container, false)

        val receiverTextView = view.findViewById<TextView>(R.id.txt_receiver)
        val amountEditText = view.findViewById<EditText>(R.id.edit_amount)
        val nextButton = view.findViewById<Button>(R.id.next_button)
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        // 초기 텍스트 설정 (금액이 없을 때도 "원" 유지)
        amountEditText.setText("0원")

        // 상주 이름 표시
        ownerName?.let {
            receiverTextView.text = "$it 님에게"
        }

        // 키패드 버튼 클릭 리스너 설정
        val keypadButtons = listOf(
            R.id.btn_1 to "1", R.id.btn_2 to "2", R.id.btn_3 to "3",
            R.id.btn_4 to "4", R.id.btn_5 to "5", R.id.btn_6 to "6",
            R.id.btn_7 to "7", R.id.btn_8 to "8", R.id.btn_9 to "9",
            R.id.btn_00 to "00", R.id.btn_0 to "0"
        )

        for ((buttonId, value) in keypadButtons) {
            view.findViewById<Button>(buttonId).setOnClickListener {
                if (amount.length < 10) { // 최대 입력 길이 제한 (10자리)
                    if (amount == "0") {
                        amount = value  // 첫 입력이면 0 제거
                    } else {
                        amount += value
                    }
                    amountEditText.setText("${formatNumber(amount.toLong())}원")  // 숫자 형식 변환
                }
            }
        }

        // X 버튼(삭제) 설정
        view.findViewById<Button>(R.id.btn_X).setOnClickListener {
            if (amount.isNotEmpty()) {
                amount = amount.dropLast(1) // 마지막 숫자 삭제
                if (amount.isEmpty()) {
                    amount = "0" // 금액이 없으면 0으로 설정
                }
                amountEditText.setText("${formatNumber(amount.toLong())}원") // 숫자 형식 변환
            }
        }

        // 뒤로 가기 버튼
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 다음 버튼 클릭 → Payment2Fragment로 이동
        nextButton.setOnClickListener {
            if (amount.isEmpty() || amount == "0") {
                Toast.makeText(requireContext(), "송금할 금액을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("Payment1Fragment", "다음 화면으로 전달 - lateId: $lateId, ownerId: $ownerId, amount: $amount")

            val payment2Fragment = Payment2Fragment().apply {
                arguments = Bundle().apply {
                    putString("late_id", lateId)
                    putString("owner_name", ownerName)
                    putInt("owner_id", ownerId ?: -1)
                    putInt("amount", amount.toInt())
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, payment2Fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    // 숫자를 3자리마다 콤마(,)를 붙이는 함수
    private fun formatNumber(number: Long): String {
        return String.format("%,d", number)
    }

    companion object {
        private const val ARG_LATE_ID = "late_id"
        private const val ARG_OWNER_NAME = "owner_name"
        private const val ARG_OWNER_ID = "owner_id"

        fun newInstance(lateId: String, ownerName: String, ownerId: Int): Payment1Fragment {
            return Payment1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LATE_ID, lateId)
                    putString(ARG_OWNER_NAME, ownerName)
                    putInt(ARG_OWNER_ID, ownerId)
                }
            }
        }
    }
}
