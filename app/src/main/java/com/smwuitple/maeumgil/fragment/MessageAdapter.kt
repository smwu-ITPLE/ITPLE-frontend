package com.smwuitple.maeumgil.fragment

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.dto.response.MessageDetail

class MessageAdapter(private val context: Context, private var messageList: List<MessageDetail>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderReceiverTextView: TextView = view.findViewById(R.id.txt_sender_receiver)
        val messageContentTextView: TextView = view.findViewById(R.id.txt_message_content)
        val imgAttachment: ImageView = view.findViewById(R.id.img_attachment)
        val videoAttachment: VideoView = view.findViewById(R.id.video_attachment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]

        // 보낸 사람 > 받는 사람
        holder.senderReceiverTextView.text = "${message.senderName} > ${message.receiverName}"

        // 메시지 내용 표시 (없으면 "내용 없음" 표시)
        holder.messageContentTextView.text = message.content ?: "내용 없음"

        // 첨부 파일 처리
        if (!message.attachment.isNullOrEmpty()) {
            val fileUri = Uri.parse("file://${message.attachment.replace("\\", "/")}")

            if (message.attachment.endsWith(".mp4") || message.attachment.endsWith(".avi")) {
                holder.videoAttachment.visibility = View.VISIBLE
                holder.imgAttachment.visibility = View.GONE
                holder.videoAttachment.setVideoURI(fileUri)
                holder.videoAttachment.setOnPreparedListener { mediaPlayer: MediaPlayer ->
                    mediaPlayer.setVolume(1f, 1f)
                    mediaPlayer.isLooping = true
                    mediaPlayer.start()
                }
            } else {
                holder.imgAttachment.visibility = View.VISIBLE
                holder.videoAttachment.visibility = View.GONE
                Glide.with(context)
                    .load(fileUri)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgAttachment)
            }
        } else {
            holder.imgAttachment.visibility = View.GONE
            holder.videoAttachment.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messageList.size
}
