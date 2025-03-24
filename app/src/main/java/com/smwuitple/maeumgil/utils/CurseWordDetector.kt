package com.smwuitple.maeumgil.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

object CurseWordDetector {

    private val curseWords = mutableListOf<String>()

    // `curse.json`에서 욕설 리스트 로드
    fun loadCurseWords(context: Context) {
        try {
            val inputStream: InputStream = context.assets.open("curse.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val jsonArray: JSONArray = jsonObject.getJSONArray("curse")

            for (i in 0 until jsonArray.length()) {
                curseWords.add(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 욕설 감지 및 변환
    fun hasCurseWords(text: String): Boolean {
        for (curse in curseWords) {
            if (text.contains(curse, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

}