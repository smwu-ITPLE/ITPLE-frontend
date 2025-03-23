package com.smwuitple.maeumgil.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.utils.CurseWordDetector

class Archieve1Fragment(private val lateId: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve1, container, false)

        val nameInput = view.findViewById<EditText>(R.id.id_text_input_edit_text)
        val messageInput = view.findViewById<EditText>(R.id.context_textview_input_edit_text)

        val closeButton = view.findViewById<TextView>(R.id.btn_close)
        val sendButton = view.findViewById<Button>(R.id.popup_btn)

        closeButton.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }

        sendButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val message = messageInput.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(context, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val senderName = if (name.isEmpty()) "익명" else name

            // 욕설 필터링 검사 (여기서만 수행)
            val (_, detected) = CurseWordDetector.filterText(message)

            if (detected) {
                // 욕설 포함된 경우 → ArchieveFailureFragment 로 바로 이동
                val failureFragment = ArchieveFailureFragment.newInstance(lateId, senderName, message)
                failureFragment.show(parentFragmentManager, "ArchieveFailureFragment")
                dismiss()
                return@setOnClickListener
            }

            // 욕설 없을 경우 → 정상적으로 Archieve2Fragment 로 이동
            val processingFragment = Archieve2Fragment.newInstance(lateId, senderName, message)
            processingFragment.show(parentFragmentManager, "Archieve2Fragment")
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
        fun newInstance(lateId: String): Archieve1Fragment {
            return Archieve1Fragment(lateId)
        }
    }
}
