package com.smwuitple.maeumgil.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.request.ArchiveRequest
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArchieveSuccessFragment(private val lateId: String, private val nickname: String, private val content: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve_success, container, false)

        val closeButton = view.findViewById<TextView>(R.id.btn_close)
        val confirmButton = view.findViewById<Button>(R.id.popup_btn)

        // API 요청: 아카이브 저장
        postArchive()

        closeButton.setOnClickListener {
            dismiss()
        }
        confirmButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun postArchive() {
        val request = ArchiveRequest(nickname, content)

        RetrofitClient.lateApi.postArchive(lateId, request).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "조문공간 아카이브 생성 성공", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.setFragmentResult("archive_update", Bundle())
                } else {
                    val errorJson = response.errorBody()?.string()
                    val jsonObject = JSONObject(errorJson)
                    val errorMessage = jsonObject.getString("message")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        fun newInstance(lateId: String, nickname: String, content: String): ArchieveSuccessFragment {
            return ArchieveSuccessFragment(lateId, nickname, content)
        }
    }
}
