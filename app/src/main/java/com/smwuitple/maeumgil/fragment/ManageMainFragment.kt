package com.smwuitple.maeumgil.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.smwuitple.maeumgil.MainActivity
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.dto.response.ProfileResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import com.smwuitple.maeumgil.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageMainFragment : Fragment() {

    private lateinit var profileTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_main, container, false)

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        profileTextView = view.findViewById(R.id.txt_memorial_message)

        loadUserProfile()

        // 뒤로 가기 버튼 클릭 시 이전 화면으로 이동
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val historyButton = view.findViewById<View>(R.id.myhistory)
        historyButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageHistoryFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }


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
            val apiService = RetrofitClient.getUserApi(requireContext())  // Retrofit 인스턴스 가져오기

            apiService.logoutUser().enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        // 성공적으로 로그아웃되면 SharedPreferences 삭제
                        val sharedPreferences = requireActivity().getSharedPreferences("cookie_prefs", 0)
                        sharedPreferences.edit().clear().apply()

                        // 로그인 화면(MainActivity)으로 이동
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }



        return view
    }

    private fun loadUserProfile() {
        val apiService = RetrofitClient.getUserApi(requireContext()) // 자동 쿠키 관리 적용된 Retrofit 사용
        apiService.profileUser().enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        profileTextView.text = "${it.name}님"
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                profileTextView.text = "사용자"
            }
        })
    }

    companion object {
        fun newInstance(): ManageMainFragment {
            return ManageMainFragment()
        }
    }
}