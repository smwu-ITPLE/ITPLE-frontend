package com.smwuitple.maeumgil.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.ApiResponse
import com.smwuitple.maeumgil.utils.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MessageFragment : Fragment() {

    private var lateId: String? = null
    private var receiverName: String? = null
    private var receiverId: Int? = null
    private var selectedVideoUri: Uri? = null

    private lateinit var receiverTextView: TextView
    private lateinit var messageInput: TextInputEditText
    private lateinit var videoUploadButton: ImageView
    private lateinit var videoPathTextView: TextView
    private lateinit var sendButton: Button
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lateId = it.getString("late_id")
            receiverName = it.getString("owner_name")
            receiverId = it.getInt("owner_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        receiverTextView = view.findViewById(R.id.txt_receiver)
        messageInput = view.findViewById(R.id.message_input)
        videoUploadButton = view.findViewById(R.id.video_upload_button)
        videoPathTextView = view.findViewById(R.id.txt_video_path)
        sendButton = view.findViewById(R.id.next_button)
        backButton = view.findViewById(R.id.back_button)

        // 수신자 이름 표시
        receiverTextView.text = "$receiverName 님에게"

        // 뒤로 가기 버튼
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 영상 업로드 버튼
        videoUploadButton.setOnClickListener {
            openGalleryForVideo()
        }

        // 메시지 전송 버튼
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun openGalleryForVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        videoPickerLauncher.launch(intent)
    }

    private val videoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedVideoUri = result.data?.data
                videoPathTextView.text = selectedVideoUri?.path ?: "업로드된 영상: 없음"
            }
        }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()

        if (messageText.isEmpty() && selectedVideoUri == null) {
            Toast.makeText(requireContext(), "메시지를 입력하거나 영상을 첨부해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (lateId.isNullOrEmpty() || receiverId == null) {
            Toast.makeText(requireContext(), "잘못된 메시지 정보입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = RetrofitClient.getLateApi(requireContext())

        // JSON 데이터 생성
        val jsonData = """
        {
            "receiverId": $receiverId,
            "content": "$messageText"
        }
    """.trimIndent()
        val requestBody = jsonData.toRequestBody("application/json".toMediaTypeOrNull())

        // 파일 첨부 처리
        var filePart: MultipartBody.Part? = null
        if (selectedVideoUri != null) {
            val fileSize = getFileSize(selectedVideoUri!!)
            if (fileSize > 2L * 1024 * 1024 * 1024) {
                Toast.makeText(requireContext(), "파일이 너무 큽니다.", Toast.LENGTH_SHORT).show()
                return
            }

            val file = File(requireContext().cacheDir, "uploaded_video.mp4")
            requireContext().contentResolver.openInputStream(selectedVideoUri!!)?.use { inputStream ->
                file.outputStream().use { output -> inputStream.copyTo(output) }
            }

            val mimeType = requireContext().contentResolver.getType(selectedVideoUri!!) ?: "video/mp4"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            filePart = MultipartBody.Part.createFormData("attachment", file.name, requestFile)
        }


        apiService.sendMessage(lateId!!, requestBody, filePart).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "조문 메시지 전송 성공!", Toast.LENGTH_SHORT).show()

                    MessageSuccessFragment.newInstance().show(parentFragmentManager, "MessageSuccessFragment")
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment.newInstance())
                        .commit()
                } else {
                    val errorJson = response.errorBody()?.string()
                    val jsonObject = JSONObject(errorJson)
                    val errorMessage = jsonObject.getString("message")
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "서버와의 연결 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getFileSize(uri: Uri): Long {
        var fileSize: Long = 0
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1) {
                it.moveToFirst()
                fileSize = it.getLong(sizeIndex)
            }
        }
        return fileSize
    }


    companion object {
        private const val ARG_LATE_ID = "late_id"
        private const val ARG_RECEIVER_NAME = "owner_name"
        private const val ARG_RECEIVER_ID = "owner_id"

        fun newInstance(lateId: String, receiverName: String, receiverId: Int): MessageFragment {
            return MessageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_LATE_ID, lateId)
                    putString(ARG_RECEIVER_NAME, receiverName)
                    putInt(ARG_RECEIVER_ID, receiverId)
                }
            }
        }
    }
}
