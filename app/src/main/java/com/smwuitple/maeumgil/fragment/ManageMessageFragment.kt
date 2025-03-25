package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ManageMessageResponse
import com.smwuitple.maeumgil.dto.response.MessageDetail
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageMessageFragment : Fragment() {

    private var lateId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter

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
        val view = inflater.inflate(R.layout.fragment_manage_message, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        lateId?.let { fetchMessages(it) } ?: Toast.makeText(requireContext(), "lateId가 없습니다.", Toast.LENGTH_SHORT).show()

        return view
    }

    private fun fetchMessages(lateId: String) {
        val apiService = RetrofitClient.getManageApi(requireContext())

        apiService.getMessageList(lateId).enqueue(object : Callback<ManageMessageResponse> {
            override fun onResponse(call: Call<ManageMessageResponse>, response: Response<ManageMessageResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        adapter = MessageAdapter(requireContext(), it)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "메시지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ManageMessageResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun updateRecyclerView(messageList: List<MessageDetail>) {
        adapter = MessageAdapter(requireContext(), messageList)
        recyclerView.adapter = adapter
    }


    companion object {
        private const val ARG_LATE_ID = "late_id"

        fun newInstance(lateId: String): ManageMessageFragment {
            return ManageMessageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LATE_ID, lateId)
                }
            }
        }
    }
}