package com.smwuitple.maeumgil.fragment

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDateToKoreanStyle(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return "날짜 없음" // 값이 없을 경우 대비

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA) // 🔥 서버에서 받은 형식
        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분", Locale.KOREA) // 🔥 원하는 출력 형식

        val date = inputFormat.parse(dateString) // String -> Date 변환
        date?.let { outputFormat.format(it) } ?: "날짜 변환 오류" // Date -> 원하는 형식의 String
    } catch (e: Exception) {
        "날짜 변환 오류" // 예외 발생 시 대비
    }
}