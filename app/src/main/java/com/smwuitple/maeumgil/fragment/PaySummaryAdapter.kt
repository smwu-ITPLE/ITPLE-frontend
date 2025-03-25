package com.smwuitple.maeumgil.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.PaySummary

class PaySummaryAdapter(private val paySumList: List<PaySummary>) :
    RecyclerView.Adapter<PaySummaryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ownerNameTextView: TextView = view.findViewById(R.id.txt_owner_name)
        val totalAmountTextView: TextView = view.findViewById(R.id.txt_total_amount)
        val percentageTextView: TextView = view.findViewById(R.id.txt_percentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pay_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val summary = paySumList[position]
        holder.ownerNameTextView.text = summary.ownerName
        holder.totalAmountTextView.text = "${summary.totalAmount}원"
        holder.percentageTextView.text = "${summary.percentage}%"
    }

    override fun getItemCount(): Int = paySumList.size
}
