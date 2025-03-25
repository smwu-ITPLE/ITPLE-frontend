package com.smwuitple.maeumgil.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smwuitple.maeumgil.MessageWebViewActivity
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.MessageDetail

class MessageAdapter(private val context: Context, private var messageList: List<MessageDetail>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderReceiverTextView: TextView = view.findViewById(R.id.txt_sender_receiver)
        val messageContentTextView: TextView = view.findViewById(R.id.txt_message_content)
        val imgAttachment: ImageView = view.findViewById(R.id.img_video_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]

        // 보낸 사람 > 받는 사람 표시
        holder.senderReceiverTextView.text = "${message.senderName} > ${message.receiverName}"

        // 메시지 내용 (없으면 "내용 없음")
        holder.messageContentTextView.text = message.content ?: "내용 없음"

        // 첨부 파일 처리
        if (!message.attachment.isNullOrEmpty()) {
            val fileUrl = message.attachment.replace("\\", "/")

            if (fileUrl.endsWith(".mp4") || fileUrl.endsWith(".avi")) {
                holder.imgAttachment.visibility = View.VISIBLE

                // Glide를 사용해서 네트워크 썸네일 자동 로드
                Glide.with(context)
                    .load(fileUrl) // 동영상 URL을 그대로 사용
                    .placeholder(R.drawable.img_message_loading) // 로딩 중 기본 이미지
                    .error(R.drawable.img_message_warning) // 오류 발생 시 기본 이미지
                    .into(holder.imgAttachment)

                // 클릭 시 WebViewActivity 실행
                holder.imgAttachment.setOnClickListener {
                    val intent = Intent(context, MessageWebViewActivity::class.java)
                    intent.putExtra("VIDEO_URL", fileUrl)
                    context.startActivity(intent)
                }
            } else {
                // 이미지 파일이라면 그대로 로드
                holder.imgAttachment.visibility = View.VISIBLE
                Glide.with(context)
                    .load(fileUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgAttachment)
            }
        } else {
            holder.imgAttachment.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messageList.size

    /**
     * 동영상의 썸네일을 가져오는 함수
     */
    private fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(videoUri, "r")?.fileDescriptor
            retriever.setDataSource(fileDescriptor)
            retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        } catch (e: Exception) {
            Log.e("ThumbnailError", "썸네일 생성 실패: ${e.message}")
            null
        } finally {
            retriever.release()
        }
    }
}
