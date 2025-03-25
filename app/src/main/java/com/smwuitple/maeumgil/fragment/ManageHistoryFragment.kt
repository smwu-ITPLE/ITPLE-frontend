package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.adapter.PayDetailAdapter
import com.smwuitple.maeumgil.dto.response.ManageMessageResponse
import com.smwuitple.maeumgil.dto.response.PayResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageHistoryFragment : Fragment() {

    private lateinit var recyclerViewMessages: RecyclerView
    private lateinit var recyclerViewTransactions: RecyclerView
    private lateinit var btnMessage: Button
    private lateinit var btnMoney: Button
    private lateinit var layoutMessage: ConstraintLayout
    private lateinit var layoutMoney: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_history, container, false)

        recyclerViewMessages = view.findViewById(R.id.recyclerView_messages)
        recyclerViewTransactions = view.findViewById(R.id.recyclerView_transactions)
        btnMessage = view.findViewById(R.id.btn_message)
        btnMoney = view.findViewById(R.id.btn_money)
        layoutMessage = view.findViewById(R.id.layout_message)
        layoutMoney = view.findViewById(R.id.layout_money)

        recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewTransactions.layoutManager = GridLayoutManager(requireContext(), 2) // Grid 적용

        btnMessage.setOnClickListener {
            showMessageLayout()
            fetchMessages()
        }

        btnMoney.setOnClickListener {
            showMoneyLayout()
            fetchPayments()
        }

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun showMessageLayout() {
        layoutMessage.visibility = View.VISIBLE
        layoutMoney.visibility = View.GONE
        recyclerViewMessages.visibility = View.VISIBLE
        recyclerViewTransactions.visibility = View.GONE
    }

    private fun showMoneyLayout() {
        layoutMessage.visibility = View.GONE
        layoutMoney.visibility = View.VISIBLE
        recyclerViewMessages.visibility = View.GONE
        recyclerViewTransactions.visibility = View.VISIBLE
    }

    private fun fetchMessages() {
        val apiService = RetrofitClient.getManageApi(requireContext())

        apiService.getMessages().enqueue(object : Callback<ManageMessageResponse> {
            override fun onResponse(call: Call<ManageMessageResponse>, response: Response<ManageMessageResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        val messageAdapter = MessageAdapter(requireContext(), it)
                        recyclerViewMessages.adapter = messageAdapter
                        recyclerViewMessages.adapter?.notifyDataSetChanged()
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

    private fun fetchPayments() {
        val apiService = RetrofitClient.getManageApi(requireContext())

        apiService.getPayments().enqueue(object : Callback<PayResponse> {
            override fun onResponse(call: Call<PayResponse>, response: Response<PayResponse>) {
                if (response.isSuccessful) {
                    response.body()?.data?.let { payList ->
                        val payAdapter = PayDetailAdapter(payList)
                        recyclerViewTransactions.adapter = payAdapter
                        recyclerViewTransactions.adapter?.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "부의금을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PayResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance(): ManageHistoryFragment {
            return ManageHistoryFragment()
        }
    }
}
