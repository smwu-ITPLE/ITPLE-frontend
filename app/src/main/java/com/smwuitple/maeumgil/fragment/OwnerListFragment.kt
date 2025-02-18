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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.LateOwnerResponse
import com.smwuitple.maeumgil.dto.response.Owner2
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OwnerListFragment : DialogFragment() {

    private var lateId: String? = null
    private var destination: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OwnerAdapter
    private val ownerList = mutableListOf<Owner2>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lateId = it.getString(ARG_LATE_ID)
            destination = it.getString(ARG_DESTINATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_owner_list, container, false)
        val closeButton = view.findViewById<TextView>(R.id.btn_close)
        val enterButton = view.findViewById<Button>(R.id.popup_btn)

        recyclerView = view.findViewById(R.id.recyclerView_host)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = OwnerAdapter(ownerList)
        recyclerView.adapter = adapter

        lateId?.let { fetchOwnerData(it) } ?: dismiss()

        closeButton.setOnClickListener {
            dismiss()
        }

        enterButton.setOnClickListener {
            val selectedOwner = adapter.getSelectedOwner()
            if (selectedOwner != null) {
                moveToNextFragment(selectedOwner)
            } else {
                Toast.makeText(requireContext(), "상주를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun fetchOwnerData(lateId: String) {
        val apiService = RetrofitClient.getLateApi(requireContext())

        apiService.getLateOwners(lateId).enqueue(object : Callback<LateOwnerResponse> {
            override fun onResponse(call: Call<LateOwnerResponse>, response: Response<LateOwnerResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        ownerList.clear()
                        ownerList.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "상주 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LateOwnerResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun moveToNextFragment(owner: Owner2) {
        val bundle = Bundle().apply {
            putString("owner_name", owner.name)
            putInt("owner_id", owner.id)
        }

        val nextFragment = when (destination) {
            "payment" -> Payment1Fragment()
            "message" -> MessageFragment()
            else -> null
        }

        nextFragment?.arguments = bundle
        nextFragment?.let {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, it) // 메인 액티비티의 프레임 레이아웃 ID
                .addToBackStack(null)
                .commit()
        }
        dismiss() // 모달 닫기
    }

    companion object {
        private const val ARG_LATE_ID = "late_id"
        private const val ARG_DESTINATION = "destination"

        fun newInstance(lateId: String, destination: String): OwnerListFragment {
            return OwnerListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LATE_ID, lateId)
                    putString(ARG_DESTINATION, destination)
                }
            }
        }
    }
}
