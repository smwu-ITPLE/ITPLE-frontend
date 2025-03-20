package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ProfileResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import com.smwuitple.maeumgil.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var profileTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        profileTextView = view.findViewById(R.id.profile_text)
        loadUserProfile()

        // 🔹 촬영 버튼 클릭 이벤트 추가
        val cameraButton = view.findViewById<View>(R.id.camera_button)
        cameraButton.setOnClickListener {
            Log.d("HomeFragment", "📸 촬영 버튼을 눌렀습니다!") // 클릭 확인 로그 추가
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CameraFragment())
                .addToBackStack(null)
                .commit()
        }


        // 조문 공간 프로필 버튼 클릭 이벤트
        val profileButton = view.findViewById<View>(R.id.profile_button)
        profileButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageMainFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // 조문 공간 생성 버튼 클릭 이벤트
        val createButton = view.findViewById<View>(R.id.create_button)
        createButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Create1Fragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // 조문 공간 검색 버튼 클릭 이벤트
        val searchButton = view.findViewById<View>(R.id.search_button)
        searchButton.setOnClickListener {
            SearchFragment.newInstance().show(parentFragmentManager, "SearchFragment")
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
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
