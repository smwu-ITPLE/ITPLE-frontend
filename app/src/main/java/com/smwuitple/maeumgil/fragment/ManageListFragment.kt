package com.smwuitple.maeumgil.fragment

import android.content.Intent
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
import com.smwuitple.maeumgil.dto.response.ShareResponse
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
                val lateId = item.id.toString()

                val manageFundFragment = ManageFundFragment.newInstance(lateId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, manageFundFragment)
                    .addToBackStack(null)  // 뒤로가기 시 이전 화면으로 돌아갈 수 있도록 추가
                    .commit()
            }
            ,
            onShareClick = { item ->
                showshare(item.id, item.name)
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

    private fun showshare(lateId: Int, lateName:String){
        val apiService = RetrofitClient.getManageApi(requireContext())
        apiService.shareLate(lateId.toString()).enqueue(object : Callback<ShareResponse>{
                override fun onResponse(call: Call<ShareResponse>, response: Response<ShareResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { data ->
                            val deepLink = "maeumgil://lates/$lateId"
                            val shareText = """
                            ${data.name}님께서 별세하셨기에 알립니다.
                            ${data.content}
                            
                            고인의 성함 : ${data.name}
                            암호 : ${data.passwd}
                            
                            초대 링크 : $deepLink
                            
                            보내는 이 : ${data.userName} (${data.userPhonenumber})
                        """.trimIndent()

                            // 공유 인텐트 실행
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            requireContext().startActivity(Intent.createChooser(shareIntent, "조문 공간 공유하기"))
                        }
                    } else {
                        Toast.makeText(requireContext(), "공유 정보 불러오기 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ShareResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
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
