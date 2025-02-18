package com.smwuitple.maeumgil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.PayDetail

class PayDetailAdapter(private val payList: List<PayDetail>) :
    RecyclerView.Adapter<PayDetailAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val envelopeImage1: ImageView = view.findViewById(R.id.img_envelope_1)
        val envelopeImage2: ImageView = view.findViewById(R.id.img_envelope_2)
        val senderReceiver1: TextView = view.findViewById(R.id.txt_sender_receiver_1)
        val senderReceiver2: TextView = view.findViewById(R.id.txt_sender_receiver_2)
        val amount1: TextView = view.findViewById(R.id.txt_amount_1)
        val amount2: TextView = view.findViewById(R.id.txt_amount_2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pay_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val firstIndex = position * 2
        val secondIndex = firstIndex + 1

        if (firstIndex < payList.size) {
            val transaction1 = payList[firstIndex]
            val envelopeDrawable1 = if (transaction1.envelope == 1) R.drawable.ic_condolence1 else R.drawable.ic_condolence2
            holder.envelopeImage1.setImageResource(envelopeDrawable1)
            holder.senderReceiver1.text = "${transaction1.senderName} > ${transaction1.receiverName}"
            holder.amount1.text = "${transaction1.amount}원"
        }

        if (secondIndex < payList.size) {
            val transaction2 = payList[secondIndex]
            val envelopeDrawable2 = if (transaction2.envelope == 1) R.drawable.ic_condolence1 else R.drawable.ic_condolence2
            holder.envelopeImage2.setImageResource(envelopeDrawable2)
            holder.senderReceiver2.text = "${transaction2.senderName} > ${transaction2.receiverName}"
            holder.amount2.text = "${transaction2.amount}원"
        } else {
            holder.envelopeImage2.visibility = View.INVISIBLE
            holder.senderReceiver2.visibility = View.INVISIBLE
            holder.amount2.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return (payList.size + 1) / 2  // 2열 구조를 위한 개수 조정
    }
}
