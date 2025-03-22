package com.smwuitple.maeumgil.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.*
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File
import java.nio.ByteBuffer

object VideoProcessor {

    fun applyFilters(context: Context, inputVideoPath: String, outputVideoPath: String, callback: (Boolean) -> Unit) {
        try {
            val frames = extractFrames(inputVideoPath)
            val processedFrames = frames.map { frame -> processFrame(frame) }
            saveProcessedVideo(context, processedFrames, outputVideoPath)
            callback(true)
        } catch (e: Exception) {
            Log.e("VideoProcessor", "Video processing failed", e)
            callback(false)
        }
    }

    private fun extractFrames(videoPath: String): List<Bitmap> {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)

        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val frameRate = 30 // FPS
        val frameTime = 1000L / frameRate

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

        val blurredMat = Mat()
        Imgproc.GaussianBlur(mat, blurredMat, Size(25.0, 25.0), 15.0)

        val grayMat = Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY)
        val grayBGRMat = Mat()
        Imgproc.cvtColor(grayMat, grayBGRMat, Imgproc.COLOR_GRAY2BGR)

        val height = mat.rows()
        val mask = Mat(mat.size(), CvType.CV_8UC1, Scalar(0.0))
        mask.rowRange(height / 2, height).setTo(Scalar(255.0))

        val maskedGray = Mat()
        Core.bitwise_and(grayBGRMat, grayBGRMat, maskedGray, mask)

        val blended = Mat()
        Core.addWeighted(mat, 0.7, blurredMat, 0.3, 0.0, blended)

        // ✅ 하단 50% 흑백 적용
        maskedGray.copyTo(blended, mask)

        val resultBitmap = Bitmap.createBitmap(blended.cols(), blended.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(blended, resultBitmap)

        mat.release()
        blurredMat.release()
        grayMat.release()
        grayBGRMat.release()
        mask.release()
        maskedGray.release()
        blended.release()

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
        mediaFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
        )

        val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        mediaCodec.start()

        val outputFile = File(outputPath)
        val muxer = MediaMuxer(outputFile.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var trackIndex = -1
        var muxerStarted = false

        val bufferInfo = MediaCodec.BufferInfo()
        var frameIndex = 0

        for (frame in frames) {
            val inputBufferIndex = mediaCodec.dequeueInputBuffer(10000)
            if (inputBufferIndex >= 0) {
                val inputBuffer = mediaCodec.getInputBuffer(inputBufferIndex)!!
                inputBuffer.clear()
                val byteArray = getNV21FromBitmap(frame, width, height)
                inputBuffer.put(byteArray)
                mediaCodec.queueInputBuffer(
                    inputBufferIndex, 0, byteArray.size, frameIndex * frameTime, 0
                )
                frameIndex++
            }

            var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
            while (outputBufferIndex >= 0) {
                val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)!!

                // ✅ muxer 준비가 안 됐으면 시작
                if (!muxerStarted) {
                    val newFormat = mediaCodec.outputFormat
                    trackIndex = muxer.addTrack(newFormat)
                    muxer.start()
                    muxerStarted = true
                }

                muxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
                mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
            }
        }

        // ✅ 마지막 EOS (End of Stream) 전달
        val eosInputBufferIndex = mediaCodec.dequeueInputBuffer(10000)
        if (eosInputBufferIndex >= 0) {
            val inputBuffer = mediaCodec.getInputBuffer(eosInputBufferIndex)
            inputBuffer?.clear()
            mediaCodec.queueInputBuffer(
                eosInputBufferIndex, 0, 0,
                frameIndex * frameTime, MediaCodec.BUFFER_FLAG_END_OF_STREAM
            )
        }

        // 남은 output buffer 처리
        var outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
        while (outputBufferIndex >= 0) {
            val outputBuffer = mediaCodec.getOutputBuffer(outputBufferIndex)!!
            muxer.writeSampleData(trackIndex, outputBuffer, bufferInfo)
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10000)
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

                val yValue = (0.299 * r + 0.587 * g + 0.114 * b).toInt().coerceIn(0, 255)
                yuv[yIndex++] = yValue.toByte()

                if (y % 2 == 0 && x % 2 == 0) {
                    val u = (-0.169 * r - 0.331 * g + 0.500 * b + 128).toInt().coerceIn(0, 255)
                    val v = (0.500 * r - 0.419 * g - 0.081 * b + 128).toInt().coerceIn(0, 255)
                    yuv[uvIndex++] = u.toByte()
                    yuv[uvIndex++] = v.toByte()
                }
            }
        }
        return yuv
    }
}
