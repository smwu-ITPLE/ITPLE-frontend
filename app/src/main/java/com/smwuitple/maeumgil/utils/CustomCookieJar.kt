package com.smwuitple.maeumgil.utils

import android.content.Context
import okhttp3.*
import java.net.CookieManager
import java.net.CookiePolicy

class CustomCookieJar(context: Context) : CookieJar {
    private val cookieManager = CookieManager(null, CookiePolicy.ACCEPT_ALL)
    private val sharedPreferences = context.getSharedPreferences("cookie_prefs", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieString = cookies.joinToString("; ") { "${it.name}=${it.value}" }
        sharedPreferences.edit().putString("JSESSIONID", cookieString).apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieString = sharedPreferences.getString("JSESSIONID", null) ?: return emptyList()
        return cookieString.split("; ").map {
            val parts = it.split("=")
            Cookie.Builder()
                .domain(url.host)
                .path("/")
                .name(parts[0])
                .value(parts[1])
                .httpOnly()
                .secure()
                .build()
        }
    }
}
