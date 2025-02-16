package com.smwuitple.maeumgil.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.request.SearchLateRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.dto.response.SearchLateResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val nameInput = view.findViewById<EditText>(R.id.id_text_input_edit_text)
        val passwdInput = view.findViewById<EditText>(R.id.password_text_input_edit_text)

        val closeButton = view.findViewById<TextView>(R.id.btn_close)
        val enterButton = view.findViewById<Button>(R.id.popup_btn)
        val jumpButton = view.findViewById<TextView>(R.id.jump_text)

        closeButton.setOnClickListener {
            dismiss()
        }

        enterButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val passwd = passwdInput.text.toString().trim()

            if (name.isEmpty() || passwd.isEmpty()) {
                Toast.makeText(context, "고인의 성함과 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = SearchLateRequest(name, passwd)
            searchPrivateLate(request)
        }

        jumpButton.setOnClickListener {
            val fragment = PrivateMainFragment.newInstance("1")

            dismissAllowingStateLoss() // 다이얼로그 닫기
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        return view
    }

    private fun searchPrivateLate(request: SearchLateRequest) {
        val apiService = RetrofitClient.getLateApi(requireContext())

        apiService.searchLate(request).enqueue(object : Callback<SearchLateResponse> {
            override fun onResponse(call: Call<SearchLateResponse>, response: Response<SearchLateResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()

                    Toast.makeText(context, result?.message ?: "조문공간 검색 성공", Toast.LENGTH_SHORT).show()

                    val lateId = result?.data

                    if (lateId != null) {
                        val fragment = PrivateMainFragment.newInstance(lateId.toInt().toString())

                        dismissAllowingStateLoss() // 다이얼로그 닫기
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(context, "조문공간에 접근할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorJson ?: "{}").optString("message", "오류 발생")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchLateResponse>, t: Throwable) {
                Toast.makeText(context, "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}
