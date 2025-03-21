package com.smwuitple.maeumgil

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class FilteredVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtered_video)

        val videoView = findViewById<VideoView>(R.id.videoView)
        val videoPath = intent.getStringExtra("videoPath") ?: return

        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.start()
    }
}
