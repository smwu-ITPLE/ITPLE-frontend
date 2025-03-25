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
import com.smwuitple.maeumgil.dto.request.LoginRequest
import com.smwuitple.maeumgil.dto.response.LoginResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import com.smwuitple.maeumgil.utils.SessionManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val phoneInput = view.findViewById<EditText>(R.id.id_text_input_edit_text)
        val passwordInput = view.findViewById<EditText>(R.id.password_text_input_edit_text)

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            val phonenumber = phoneInput.text.toString().trim()
            val passwd = passwordInput.text.toString().trim()

            if (phonenumber.isEmpty() || passwd.isEmpty()) {
                Toast.makeText(context, "전화번호와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(phonenumber, passwd)

            val apiService = RetrofitClient.getUserApi(requireContext())
            apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    val headers = response.headers()
                    val cookies = headers.values("Set-Cookie")

                    for (cookie in cookies) {
                        if (cookie.startsWith("JSESSIONID")) {
                            val sessionId = cookie.split(";")[0] // JSESSIONID=xxxx
                            SessionManager.saveSessionId(requireContext(), sessionId)
                        }
                    }

                    if (response.isSuccessful) {
                        val result = response.body()
                        Toast.makeText(context, result?.message ?: "로그인 성공", Toast.LENGTH_SHORT).show()

                        // 로그인 성공 후 홈 화면으로 이동
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment.newInstance())
                            .commit()
                    } else {
                        val errorJson = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorJson)
                        val errorMessage = jsonObject.getString("message")
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(context, "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}
