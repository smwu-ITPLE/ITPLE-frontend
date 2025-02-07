package com.smwuitple.maeumgil.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.EnterLateResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class PrivateMainFragment : Fragment() {

    private var lateId: String? = null  // lateId 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lateId = it.getString(ARG_LATE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_private_main, container, false)

        // UI 요소 바인딩
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        val profileImageView = view.findViewById<ImageView>(R.id.memorial_image_view)
        val nameTextView = view.findViewById<TextView>(R.id.txt_name)
        val ageTextView = view.findViewById<TextView>(R.id.txt_age)
        val passDateTextView = view.findViewById<TextView>(R.id.txt_date_input1)

        val locationTextView = view.findViewById<TextView>(R.id.txt_location_input)
        val deathDateTextView = view.findViewById<TextView>(R.id.txt_date2_input)
        val ownersListContainer = view.findViewById<LinearLayout>(R.id.owners_list_container)
        val archivesListContainer = view.findViewById<LinearLayout>(R.id.archives_list_container)

        val chatButton = view.findViewById<Button>(R.id.btn_chat)
        val donationButton = view.findViewById<Button>(R.id.btn_chat2)
        val writeButton = view.findViewById<ImageView>(R.id.write_button)

        // 뒤로 가기 버튼 클릭 시 이전 화면으로 이동
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // API 데이터 가져오기
        lateId?.let {
            loadPrivateMainData(
                it, profileImageView, nameTextView, ageTextView, passDateTextView,
                locationTextView, deathDateTextView, ownersListContainer, archivesListContainer)
        } ?: run {
            Toast.makeText(requireContext(), "lateId가 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // 조문 메시지 버튼 클릭
        chatButton.setOnClickListener {

        }

        // 부의금 송금 버튼 클릭
        donationButton.setOnClickListener {

        }

        // 아카이브 작성 버튼 클릭
        writeButton.setOnClickListener {

        }
        return view
    }

    private fun loadPrivateMainData(
        lateId: String, profileImageView: ImageView, nameTextView: TextView, ageTextView: TextView,
        passDateTextView: TextView, locationTextView: TextView, deathDateTextView: TextView,
        ownersListContainer: LinearLayout, archivesListContainer: LinearLayout
    ) {
        val apiService = RetrofitClient.lateApi

        apiService.enterLate(lateId).enqueue(object : Callback<EnterLateResponse> {
            override fun onResponse(call: Call<EnterLateResponse>, response: Response<EnterLateResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    data?.let {
                        // 고인 기본 정보 업데이트
                        nameTextView.text = "故 ${it.name}"
                        ageTextView.text = "향년 ${it.age}세 ${it.gender}"
                        passDateTextView.text = formatDateToKoreanStyle(it.datePass)
                        locationTextView.text = it.location
                        deathDateTextView.text = formatDateToKoreanStyle(it.dateDeath)

                        // 프로필 이미지 로드 (Glide 사용)
                        Glide.with(requireContext()).load(it.profile).into(profileImageView)

                        // 상주 정보 리스트 추가
                        ownersListContainer.removeAllViews()
                        for (owner in it.owners) {
                            val ownerLayout = LinearLayout(requireContext()).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                orientation = LinearLayout.HORIZONTAL
                            }

                            val relationTextView = TextView(requireContext()).apply {
                                text = owner.relation
                                textSize = 15f
                                setTextColor(resources.getColor(R.color.black, null))
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply { setMargins(60, 10, 0, 10) }
                            }

                            val nameTextView = TextView(requireContext()).apply {
                                text = owner.name
                                textSize = 15f
                                setTextColor(resources.getColor(R.color.black, null))
                                layoutParams = LinearLayout.LayoutParams(
                                    0,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    weight = 1f
                                    setMargins(0, 10, 60, 10)
                                }
                                textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                            }

                            ownerLayout.addView(relationTextView)
                            ownerLayout.addView(nameTextView)
                            ownersListContainer.addView(ownerLayout)
                        }

                        // 아카이브 메시지 리스트 추가 (랜덤 배경 적용)
                        archivesListContainer.removeAllViews()
                        val backgroundColors = listOf("#FFF764", "#FFC7CA", "#A1C4FD", "#FF9A8B", "#D4FC79")

                        for (archive in it.archives) {
                            val randomColor = backgroundColors[Random.nextInt(backgroundColors.size)]
                            val archiveTextView = TextView(requireContext()).apply {
                                text = "${archive.nickname}\n${archive.content}"
                                textSize = 15f
                                setPadding(10, 10, 10, 10)
                                setTextColor(Color.BLACK)
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(10, 5, 10, 10)
                                }
                                setBackgroundColor(Color.parseColor(randomColor))
                            }
                            archivesListContainer.addView(archiveTextView)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "조문공간이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EnterLateResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val ARG_LATE_ID = "late_id"

        fun newInstance(lateId: String): PrivateMainFragment {
            return PrivateMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LATE_ID, lateId)
                }
            }
        }
    }
}
