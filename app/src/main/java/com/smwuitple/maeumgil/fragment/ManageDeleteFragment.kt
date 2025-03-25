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
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageDeleteFragment(
    private val lateId: Int,
    private val lateName: String,
    private val onDeleteSuccess: () -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_delete, container, false)

        val closeButton = view.findViewById<TextView>(R.id.btn_close)
        val cancelButton = view.findViewById<Button>(R.id.popup_btn1)
        val deleteButton = view.findViewById<Button>(R.id.popup_btn2)
        val memorialTextView = view.findViewById<TextView>(R.id.txt_success_message)

        // 조문 공간 이름 업데이트
        memorialTextView.text = "故 ${lateName}님의"

        // 닫기 및 취소 버튼 클릭 시 아무 변화 없이 모달 닫기
        closeButton.setOnClickListener { dismiss() }
        cancelButton.setOnClickListener { dismiss() }

        // 삭제 버튼 클릭 시 API 호출
        deleteButton.setOnClickListener { deleteMemorialSpace() }

        return view
    }

    private fun deleteMemorialSpace() {
        val apiService = RetrofitClient.getLateApi(requireContext())

        apiService.deleteLate(lateId.toString()).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "조문 공간 삭제 성공", Toast.LENGTH_SHORT).show()
                    // 삭제 성공 후 ManageListFragment에 알림
                    onDeleteSuccess()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "삭제 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        fun newInstance(lateId: Int, lateName: String, onDeleteSuccess: () -> Unit): ManageDeleteFragment {
            return ManageDeleteFragment(lateId, lateName, onDeleteSuccess)
        }
    }
}
