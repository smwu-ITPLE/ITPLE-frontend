package com.smwuitple.maeumgil.fragment

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.Owner2

class OwnerAdapter(private var ownerList: List<Owner2>) :
    RecyclerView.Adapter<OwnerAdapter.OwnerViewHolder>() {

    private var selectedPosition = -1 // 단일 선택을 위한 변수

    class OwnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_select)
        val nameTextView: TextView = itemView.findViewById(R.id.txt_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.host_item, parent, false)
        return OwnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: OwnerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val owner = ownerList[position]
        holder.nameTextView.text = owner.name

        // 체크박스 상태 업데이트 (단일 선택)
        holder.checkBox.isChecked = (position == selectedPosition)

        // 체크박스 클릭 리스너 (단일 선택 기능 적용)
        holder.checkBox.setOnClickListener {
            if (selectedPosition != position) {
                // 기존 선택 해제 후 새 선택 설정
                val previousPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousPosition) // 이전 선택된 항목 해제
                notifyItemChanged(selectedPosition) // 새로 선택된 항목 업데이트
            }
        }
    }

    override fun getItemCount(): Int = ownerList.size

    fun updateData(newList: List<Owner2>) {
        ownerList = newList
        selectedPosition = -1 // 새로운 데이터가 들어오면 선택 초기화
        notifyDataSetChanged()
    }

    fun getSelectedOwner(): Owner2? {
        return if (selectedPosition != -1) ownerList[selectedPosition] else null
    }
}
