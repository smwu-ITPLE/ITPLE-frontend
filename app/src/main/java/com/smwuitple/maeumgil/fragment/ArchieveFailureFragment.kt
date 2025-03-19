package com.smwuitple.maeumgil.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.smwuitple.maeumgil.R

class ArchieveFailureFragment() : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve_failure, container, false)

        val closeButton = view.findViewById<TextView>(R.id.btn_close)
        val confirmButton = view.findViewById<Button>(R.id.popup_btn)

        closeButton.setOnClickListener {
            dismiss()
        }
        confirmButton.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    companion object {
        fun newInstance(lateId: String, nickname: String, content: String): ArchieveFailureFragment {
            val fragment = ArchieveFailureFragment()
            val args = Bundle()
            args.putString("LATE_ID", lateId)
            args.putString("NICKNAME", nickname)
            args.putString("CONTENT", content)
            fragment.arguments = args
            return fragment
        }
    }
}
