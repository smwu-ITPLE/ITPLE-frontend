package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.request.SignupRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val nameInput = view.findViewById<EditText>(R.id.name_textview_input_edit_text)
        val phoneInput = view.findViewById<EditText>(R.id.id_text_input_edit_text)
        val passwordInput = view.findViewById<EditText>(R.id.password_text_input_edit_text)
        val passwordRecheckInput = view.findViewById<EditText>(R.id.password_recheck_text_input_edit_text)

        val signupButton = view.findViewById<Button>(R.id.btn_signup)
        signupButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phonenumber = phoneInput.text.toString().trim()
            val passwd = passwordInput.text.toString().trim()
            val passwdRecheck = passwordRecheckInput.text.toString().trim()

            if (name.isEmpty() || phonenumber.isEmpty() || passwd.isEmpty() || passwdRecheck.isEmpty()) {
                Toast.makeText(context, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$".toRegex()
            if (!passwordPattern.matches(passwd)) {
                Toast.makeText(context, "비밀번호는 영문과 숫자를 포함한 8자 이상이어야 합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwd != passwdRecheck) {
                Toast.makeText(context, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = SignupRequest(name, phonenumber, passwd)

            val apiService = RetrofitClient.getUserApi(requireContext())
            apiService.signupUser(request).enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Toast.makeText(context, result?.message ?: "회원가입 성공", Toast.LENGTH_SHORT).show()

                        SignToLoginFragment.newInstance().show(parentFragmentManager, "SignToLoginFragment")

                    } else {
                        val errorJson = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorJson)
                        val errorMessage = jsonObject.getString("message")
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(context, "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    companion object {
        fun newInstance(): SignupFragment {
            return SignupFragment()
        }
    }
}
