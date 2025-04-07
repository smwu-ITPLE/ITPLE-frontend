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
import com.smwuitple.maeumgil.network.ApiClient
import com.smwuitple.maeumgil.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class Archieve1Fragment(private val lateId: String) : DialogFragment() {

    private val apiService = ApiClient.textApiService

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
            dismiss()
        }

        sendButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val message = messageInput.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(context, "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val senderName = if (name.isEmpty()) "익명" else name

            // 서버로 욕설 감지 API 요청
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.detectText(mapOf("text" to message))
                    withContext(Dispatchers.Main) {
                        if (response.detected) {
                            val failedFragment = Archieve2Fragment.newInstance(lateId, senderName, message, isFailed = true)
                            failedFragment.show(parentFragmentManager, "Archieve2Fragment")
                        } else {
                            val successFragment = Archieve2Fragment.newInstance(lateId, senderName, message)
                            successFragment.show(parentFragmentManager, "Archieve2Fragment")
                        }
                        dismiss()
                    }
                } catch (e: HttpException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "욕설 감지 서버 오류", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
