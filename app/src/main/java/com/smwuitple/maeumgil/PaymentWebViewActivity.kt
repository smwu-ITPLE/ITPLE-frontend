package com.smwuitple.maeumgil

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class PaymentWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_webview)

        webView = findViewById(R.id.paymentWebView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true

        // Intent에서 결제 URL 가져오기
        val paymentUrl = intent.getStringExtra("payment_url") ?: ""
        webView.loadUrl(paymentUrl)

        // WebViewClient 설정
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()

                // 결제 성공 시 pg_token 포함 URL 감지
                if (url.contains("pg_token")) {
                    val pgToken = Uri.parse(url).getQueryParameter("pg_token")

                    val intent = Intent()
                    intent.putExtra("pg_token", pgToken)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    return true
                }

                return false
            }
        }
    }
}
