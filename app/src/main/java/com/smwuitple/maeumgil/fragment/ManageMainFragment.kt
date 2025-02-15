package com.smwuitple.maeumgil.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.smwuitple.maeumgil.MainActivity
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageMainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_main, container, false)

        // 조문 공간 프로필 버튼 클릭 이벤트
        val settingButton = view.findViewById<View>(R.id.setting)
        settingButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageListFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // 로그아웃 버튼
        val logoutButton = view.findViewById<View>(R.id.logout)
        logoutButton.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", 0)
            val sessionId = sharedPreferences.getString("JSESSIONID", null)

            if (sessionId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "로그인 세션이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val apiService = RetrofitClient.userApi

            apiService.logoutUser("JSESSIONID=$sessionId").enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        // 로그아웃 성공 -> SharedPreferences 삭제 & 홈화면으로 이동
                        sharedPreferences.edit().clear().apply()

                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }

    companion object {
        fun newInstance(): ManageMainFragment {
            return ManageMainFragment()
        }
    }
}