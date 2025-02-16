package com.smwuitple.maeumgil.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ShareResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Create4Fragment : Fragment() {

    private lateinit var memorialImageView: ImageView
    private lateinit var memorialMessage: TextView
    private lateinit var shareButton: Button
    private lateinit var homeButton: Button
    private lateinit var manageButton: Button

    private var profileUri: String? = null
    private var memorialName: String? = null
    private var lateId: String? = null  // 조문 공간 ID 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        memorialImageView = view.findViewById(R.id.memorial_image_view)
        memorialMessage = view.findViewById(R.id.txt_memorial_message)
        shareButton = view.findViewById(R.id.share_button)
        homeButton = view.findViewById(R.id.home_button)
        manageButton = view.findViewById(R.id.manage_button)

        // 전달된 데이터 받기
        memorialName = arguments?.getString("name") ?: "故 눈송이"
        profileUri = arguments?.getString("profile")
        lateId = arguments?.getString("lateId") // 생성된 조문공간 ID 받아오기

        println(" Create4Fragment - late $lateId")

        memorialMessage.text = "$memorialName 님의\n조문 공간이 생성되었습니다"

        // 프로필 이미지 로드
        if (!profileUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(profileUri))
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(memorialImageView)
        } else {
            memorialImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // 공유 버튼 클릭 이벤트
        shareButton.setOnClickListener {
            if (lateId != null) {
                shareMemorialSpace(lateId!!)
            } else {
                Toast.makeText(requireContext(), "조문 공간 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 홈 버튼 클릭 이벤트 (홈으로 이동)
        homeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }

        // 관리 버튼 클릭 이벤트 (관리 화면으로 이동 가능)
        manageButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageListFragment.newInstance())
                .commit()
        }
    }

    private fun shareMemorialSpace(lateId: String) {
        val apiService = RetrofitClient.getManageApi(requireContext())
        apiService.shareLate(lateId).enqueue(object : Callback<ShareResponse> {
            override fun onResponse(call: Call<ShareResponse>, response: Response<ShareResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        // 딥링크 설정
                        val deepLink = "maeumgil://lates/$lateId"

                        val shareText = """
                            ${data.name}님께서 별세하셨기에 알립니다.
                            ${data.content}
                            
                            고인의 성함 : ${data.name}
                            암호 : ${data.passwd}
                            
                            초대 링크 : $deepLink
                            
                            보내는 이 : ${data.userName} (${data.userPhonenumber})
                        """.trimIndent()

                        // 공유 인텐트 실행
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        requireContext().startActivity(Intent.createChooser(shareIntent, "조문 공간 공유하기"))
                    }
                } else {
                    Toast.makeText(requireContext(), "공유 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ShareResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance(name: String, profile: String?, lateId: String): Create4Fragment {
            val fragment = Create4Fragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("profile", profile)
            args.putString("lateId", lateId)
            fragment.arguments = args
            return fragment
        }
    }
}
