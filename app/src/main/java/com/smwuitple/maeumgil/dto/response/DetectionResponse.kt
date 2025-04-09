package com.smwuitple.maeumgil.dto.response

data class DetectionResponse(
    val detected: Boolean,
    val filtered_text: String? = null,
    val original_text: String? = null
)
