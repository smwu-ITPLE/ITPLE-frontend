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
import com.smwuitple.maeumgil.adapter.PayDetailAdapter
import com.smwuitple.maeumgil.dto.response.ManagePayResponse
import com.smwuitple.maeumgil.dto.response.PayDetail
import com.smwuitple.maeumgil.dto.response.PaySummary
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageFundFragment : Fragment() {

    private var lateId: String? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PaySummaryAdapter

    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionAdapter: PayDetailAdapter

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
        val view = inflater.inflate(R.layout.fragment_manage_fund, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        transactionRecyclerView = view.findViewById(R.id.recyclerView_transactions)
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        lateId?.let { fetchManageFundData(it) }
            ?: Toast.makeText(requireContext(), "lateId가 없습니다.", Toast.LENGTH_SHORT).show()

        return view
    }

    private fun fetchManageFundData(lateId: String) {
        val apiService = RetrofitClient.getManageApi(requireContext())

        apiService.getPayList(lateId).enqueue(object : Callback<ManagePayResponse> {
            override fun onResponse(call: Call<ManagePayResponse>, response: Response<ManagePayResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        updateRecyclerView(it.paySumList)
                        updateRecentTransactions(it.payList)
                    }
                } else {
                    Toast.makeText(requireContext(), "부의금 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ManagePayResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecyclerView(paySumList: List<PaySummary>) {
        val roundedPaySumList = paySumList.map { summary ->
            summary.copy(percentage = String.format("%.1f", summary.percentage).toDouble()) // 소수점 첫째 자리까지만 반올림
        }
        adapter = PaySummaryAdapter(roundedPaySumList)
        recyclerView.adapter = adapter
    }

    private fun updateRecentTransactions(payList: List<PayDetail>) {
        transactionAdapter = PayDetailAdapter(payList)
        transactionRecyclerView.adapter = transactionAdapter
    }

    companion object {
        private const val ARG_LATE_ID = "late_id"

        fun newInstance(lateId: String): ManageFundFragment {
            return ManageFundFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LATE_ID, lateId)
                }
            }
        }
    }
}