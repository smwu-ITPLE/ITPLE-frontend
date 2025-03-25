package com.smwuitple.maeumgil.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import com.smwuitple.maeumgil.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class Create3Fragment : Fragment() {
    private lateinit var ownerView: LinearLayout
    private lateinit var addOwnerButton: ImageView
    private lateinit var nextButton: Button
    private lateinit var backButton: ImageView

    private val ownerList = mutableListOf<Map<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ownerView = view.findViewById(R.id.add_card)
        addOwnerButton = view.findViewById(R.id.add_owner_button)
        nextButton = view.findViewById(R.id.next_button)
        backButton = view.findViewById(R.id.back_button)

        val name = arguments?.getString("name")
        val age = arguments?.getInt("age")
        val gender = arguments?.getString("gender")
        val datePass = arguments?.getString("datePass")
        val profilePath = arguments?.getString("profile")
        val dateDeath = arguments?.getString("dateDeath")
        val location = arguments?.getString("location")
        val content = arguments?.getString("content")

        addOwnerButton.setOnClickListener {
            addNewOwnerCard()
        }

        nextButton.setOnClickListener {
            sendCreateLateRequest(name, age, gender, datePass, profilePath, dateDeath, location, content)
        }

        backButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager

            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun addNewOwnerCard() {
        val ownerCard = LayoutInflater.from(requireContext()).inflate(R.layout.late_owner_card, ownerView, false)
        ownerView.addView(ownerCard) // ScrollView 내부에 카드 추가
    }

    private fun sendCreateLateRequest(
        name: String?, age: Int?, gender: String?, datePass: String?, profilePath: String?,
        dateDeath: String?, location: String?, content: String?
    ) {
        ownerList.clear()

        // `ownerView`에서 자식 뷰들 가져오기
        for (i in 0 until ownerView.childCount) {
            val ownerCardView = ownerView.getChildAt(i)

            val ownerName = ownerCardView.findViewById<EditText>(R.id.id_text_input_edit_text).text.toString().trim()
            val ownerRelation = ownerCardView.findViewById<EditText>(R.id.relation_text_input_edit_text).text.toString().trim()
            val ownerPhone = ownerCardView.findViewById<EditText>(R.id.phone_text_input_edit_text).text.toString().trim()

            if (ownerName.isEmpty() || ownerRelation.isEmpty() || ownerPhone.isEmpty()) {
                Toast.makeText(requireContext(), "모든 상주 정보를 입력하세요", Toast.LENGTH_SHORT).show()
                return
            }

            ownerList.add(mapOf("name" to ownerName, "relation" to ownerRelation, "phoneNumber" to ownerPhone))
        }

        val jsonObject = JSONObject().apply {
            put("name", name)
            put("age", age ?: 0)
            put("gender", gender)
            put("datePass", datePass)
            put("dateDeath", dateDeath)
            put("location", location)
            put("content", content)
            put("owners", JSONArray(ownerList))
        }

        val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
        Log.d("API_REQUEST", "전송 데이터: $jsonObject")

        val profilePart = profilePath?.let { uriString ->
            val uri = Uri.parse(uriString)
            val file = File(requireContext().cacheDir, "profile_image.jpg")
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { output -> inputStream.copyTo(output) }
            }
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("profile", file.name, requestFile)
        }

        val apiService = RetrofitClient.getLateApi(requireContext())

        apiService.createLate(requestBody, profilePart).enqueue(object :
            Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    Toast.makeText(requireContext(), result?.message ?: "조문 공간 생성 성공", Toast.LENGTH_SHORT).show()

                    try {
                        val data = result?.data as? Map<String, Any>
                        val id = (data?.get("id") as? Number)?.toInt()?.toString() ?: ""
                        val name = data?.get("name")?.toString() ?: ""
                        val profile = data?.get("profile")?.toString() ?: ""

                        val bundle = Bundle().apply {
                            putString("lateId", id)
                            putString("name", name)
                            putString("profile", profile)
                        }

                        val fragment = Create4Fragment()
                        fragment.arguments = bundle

                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .commit()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "응답 데이터 처리 오류", Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", "JSON 파싱 오류: ${e.message}")
                    }

                } else {
                    val errorJson = response.errorBody()?.string()
                    try {
                        val jsonObject = JSONObject(errorJson)
                        val errorMessage = jsonObject.getString("message")
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "오류 발생", Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", "오류 응답 처리 실패: ${e.message}")
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
