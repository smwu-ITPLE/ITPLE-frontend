package com.smwuitple.maeumgil.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.LateListItem

class LateListAdapter(
    private val onManageMessagesClick: (LateListItem) -> Unit,
    private val onManageDonationsClick: (LateListItem) -> Unit,
    private val onDeleteClick: (LateListItem) -> Unit,
    private val onShareClick: (LateListItem) -> Unit
) : RecyclerView.Adapter<LateListAdapter.LateListViewHolder>() {

    private val lateList = mutableListOf<LateListItem>()

    // 리스트 업데이트 메서드 추가
    fun setLateList(newList: List<LateListItem>) {
        lateList.clear()
        lateList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class LateListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memorialName: TextView = itemView.findViewById(R.id.txt_memorial_name)
        val btnShare: ImageButton = itemView.findViewById(R.id.btn_share)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
        val btnManageMessages: Button = itemView.findViewById(R.id.btn_manage_messages)
        val btnManageDonations: Button = itemView.findViewById(R.id.btn_manage_donations)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LateListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_late_list, parent, false)
        return LateListViewHolder(view)
    }

    override fun onBindViewHolder(holder: LateListViewHolder, position: Int) {
        val item = lateList[position]
        holder.memorialName.text = "故 ${item.name}님의 조문 공간"

        // 메시지 관리 버튼 클릭 이벤트
        holder.btnManageMessages.setOnClickListener {
            onManageMessagesClick(item)
        }

        // 부의금 관리 버튼 클릭 이벤트
        holder.btnManageDonations.setOnClickListener {
            onManageDonationsClick(item)
        }

        // 공유 버튼 클릭 이벤트
        holder.btnShare.setOnClickListener {
            onShareClick(item)
        }

        // 삭제 버튼 클릭 이벤트
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int {
        return lateList.size
    }
}
