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
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.dto.response.LateListItem
import com.smwuitple.maeumgil.utils.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var lateListAdapter: LateListAdapter
    private val lateList = mutableListOf<LateListItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lateListAdapter = LateListAdapter(
            onManageMessagesClick = { item ->
                Toast.makeText(requireContext(), "${item.name} 메시지 관리", Toast.LENGTH_SHORT).show()
            },
            onManageDonationsClick = { item ->
                Toast.makeText(requireContext(), "${item.name} 부의금 관리", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { item ->
                val fragment = ManageShareFragment.newInstance(item.id)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { item ->
                showDeleteDialog(item.id, item.name)
            }
        )

        recyclerView.adapter = lateListAdapter
        loadLateList()

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadLateList() {
        val apiService = RetrofitClient.getManageApi(requireContext())
        apiService.getLateList().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val lateListResponse = response.body()?.data as? List<Map<String, Any>>
                    lateList.clear()
                    lateListResponse?.forEach {
                        lateList.add(
                            LateListItem(
                                id = (it["id"] as? Number)?.toInt() ?: 0,
                                name = it["name"] as? String ?: ""
                            )
                        )
                    }
                    lateListAdapter.setLateList(lateList)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 오류", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteDialog(lateId: Int, lateName: String) {
        val deleteDialog = ManageDeleteFragment.newInstance(lateId, lateName) {
            loadLateList() // 삭제 후 리스트 갱신
        }
        deleteDialog.show(parentFragmentManager, "ManageDeleteFragment")
    }

    companion object {
        fun newInstance(): ManageListFragment {
            return ManageListFragment()
        }
    }
}
