package com.smwuitple.maeumgil.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.smwuitple.maeumgil.R

class Create4Fragment : Fragment() {

    private lateinit var memorialImageView: ImageView
    private lateinit var memorialMessage: TextView
    private lateinit var shareButton: Button
    private lateinit var homeButton: Button
    private lateinit var manageButton: Button

    private var profileUri: String? = null
    private var memorialName: String? = null

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
        memorialName = arguments?.getString("name") ?: "눈송이"
        profileUri = arguments?.getString("profile") // null 가능
        memorialMessage.text = "故 $memorialName 님의\n조문 공간이 생성되었습니다"

        // 프로필 이미지 로드
        if (!profileUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(profileUri))
                .placeholder(R.drawable.ic_launcher_foreground) // 기본 이미지
                .into(memorialImageView)
        } else {
            memorialImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // 공유 버튼 클릭 이벤트
        shareButton.setOnClickListener {
            // 공유 로직
        }

        // 홈 버튼 클릭 이벤트 (홈으로 이동)
        homeButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }

        // 관리 버튼 클릭 이벤트 (관리 화면으로 이동 가능)
        manageButton.setOnClickListener {
            // 관리 화면으로 이동하는 로직 추가 가능
        }
    }
}
