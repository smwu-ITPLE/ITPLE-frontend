package com.smwuitple.maeumgil

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MessageWebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        // 동영상 URL 가져오기
        val videoUrl = intent.getStringExtra("VIDEO_URL") ?: return

        // WebView 설정
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // 자바스크립트 활성화
        webSettings.mediaPlaybackRequiresUserGesture = false // 자동 재생 허용
        webSettings.allowFileAccess = true // 파일 접근 허용
        webSettings.domStorageEnabled = true // 로컬 저장소 사용 허용

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(videoUrl)
    }
}
