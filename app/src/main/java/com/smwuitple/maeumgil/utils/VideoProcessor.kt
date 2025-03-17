package com.smwuitple.maeumgil.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.*
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

object VideoProcessor {

    fun applyFilters(context: Context, inputVideoPath: String, outputVideoPath: String) {
        val frames = extractFrames(inputVideoPath)
        val processedFrames = frames.map { frame -> processFrame(frame) }
        saveProcessedVideo(context, processedFrames, outputVideoPath)
    }

    private fun extractFrames(videoPath: String): List<Bitmap> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val frameRate = 30 // FPS 설정
        val frameTime = 1000L / frameRate // ms 단위

        val frames = mutableListOf<Bitmap>()
        for (time in 0L until duration step frameTime) {
            val frame = retriever.getFrameAtTime(time * 1000L, MediaMetadataRetriever.OPTION_CLOSEST)
            frame?.let { frames.add(it) }
        }
        retriever.release()
        return frames
    }

    private fun processFrame(frame: Bitmap): Bitmap {
        val mat = Mat()
        Utils.bitmapToMat(frame, mat)

        // ✅ 원본 배경 블러 적용
        val blurredMat = Mat()
        Imgproc.GaussianBlur(mat, blurredMat, Size(25.0, 25.0), 15.0)

        // ✅ 흑백 변환 (확실히 적용)
        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)
        val grayBGRMat = Mat()
        Imgproc.cvtColor(grayMat, grayBGRMat, Imgproc.COLOR_GRAY2BGR) // 다시 BGR로 변환

        // ✅ 하단 50% 마스크 생성
        val height = mat.rows()
        val mask = Mat(mat.size(), CvType.CV_8UC1, Scalar(0.0)) // 기본 0 (검은색)
        mask.rowRange(height / 2, height).setTo(Scalar(255.0)) // 하단 50%를 255로 설정

        val maskedGray = Mat()
        Core.bitwise_and(grayBGRMat, grayBGRMat, maskedGray, mask) // 흑백을 하단 50%에 적용

        val resultMat = Mat()
        Core.bitwise_or(mat, maskedGray, resultMat) // ⬅️ 흑백과 원본을 합성 (addWeighted 제거)

        // ✅ 블러를 일부 영역에만 적용하여 원본과 혼합
        Core.addWeighted(resultMat, 0.7, blurredMat, 0.3, 0.0, resultMat)

        // ✅ 결과 저장
        val resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultMat, resultBitmap)

        // 메모리 해제
        mat.release()
        blurredMat.release()
        grayMat.release()
        grayBGRMat.release()
        mask.release()
        maskedGray.release()
        resultMat.release()

        return resultBitmap
    }


    private fun saveProcessedVideo(context: Context, frames: List<Bitmap>, outputPath: String) {
        val width = frames[0].width
        val height = frames[0].height
        val frameRate = 30
        val frameTime = 1000000L / frameRate

        val mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 4000000)
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)

        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec.start()

        val outputFile = File(outputPath)
        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val trackIndex = muxer.addTrack(mediaCodec.outputFormat)
        muxer.start()

        val bufferInfo = MediaCodec.BufferInfo()
        var frameIndex = 0

        for (frame in frames) {
            val inputBufferIndex = mediaCodec.dequeueInputBuffer(10000)
            if (inputBufferIndex >= 0) {
                val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)!!
                inputBuffer.clear()
                val byteArray = getNV21FromBitmap(frame, width, height)
                inputBuffer.put(byteArray)
                mediaCodec.queueInputBuffer(inputBufferIndex, 0, byteArray.size, frameIndex * frameTime, 0)
                frameIndex++
            }

            var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
            while (outputBufferIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)!!
                muxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
            }
        }

        mediaCodec.stop()
        mediaCodec.release()
        muxer.stop()
        muxer.release()

        Log.d("VideoProcessor", "✅ Filtered video saved at: $outputPath")
    }

    private fun getNV21FromBitmap(bitmap: Bitmap, width: Int, height: Int): ByteArray {
        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)
        val yuv = ByteArray(width * height * 3 / 2)

        var yIndex = 0
        var uvIndex = width * height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val color = argb[y * width + x]
                val r = (color shr 16) and 0xFF
                val g = (color shr 8) and 0xFF
                val b = color and 0xFF

                val yValue = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                yuv[yIndex++] = yValue.toByte()

                if (y % 2 == 0 && x % 2 == 0) {
                    val u = (-0.169 * r - 0.331 * g + 0.500 * b + 128).toInt()
                    val v = (0.500 * r - 0.419 * g - 0.081 * b + 128).toInt()
                    yuv[uvIndex++] = u.toByte()
                    yuv[uvIndex++] = v.toByte()
                }
            }
        }
        return yuv
    }
} 