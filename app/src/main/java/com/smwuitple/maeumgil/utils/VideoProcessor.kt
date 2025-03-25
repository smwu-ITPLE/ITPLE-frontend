package com.smwuitple.maeumgil.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.*
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.File

object VideoProcessor {

    fun applyFilters(context: Context, inputVideoPath: String, outputVideoPath: String, callback: (Boolean) -> Unit) {
        Log.d("VideoProcessor", "🔥 Start processing video: $inputVideoPath")
        try {
            val frames = extractFrames(inputVideoPath)
            val processedFrames = frames.mapIndexed { idx, frame ->
                Log.d("VideoProcessor", "Processing frame ${idx + 1}/${frames.size}")
                processFrame(frame)
            }
            saveProcessedVideo(processedFrames, outputVideoPath)
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
        val frameRate = 30
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
        val rgbaMat = Mat()
        Utils.bitmapToMat(frame, rgbaMat) // RGBA로

        val bgrMat = Mat()
        Imgproc.cvtColor(rgbaMat, bgrMat, Imgproc.COLOR_RGBA2BGR)

        val blurredMat = Mat()
        Imgproc.GaussianBlur(bgrMat, blurredMat, Size(25.0, 25.0), 15.0)

        val grayMat = Mat()
        Imgproc.cvtColor(bgrMat, grayMat, Imgproc.COLOR_BGR2GRAY)
        val grayBGRMat = Mat()
        Imgproc.cvtColor(grayMat, grayBGRMat, Imgproc.COLOR_GRAY2BGR)

        val height = bgrMat.rows()
        val mask = Mat(bgrMat.size(), CvType.CV_8UC1, Scalar(0.0))
        mask.rowRange(height / 2, height).setTo(Scalar(255.0)) // 아랫부분만 흑백

        val maskedGray = Mat()
        Core.bitwise_and(grayBGRMat, grayBGRMat, maskedGray, mask)

        val blended = Mat()
        Core.addWeighted(bgrMat, 0.7, blurredMat, 0.3, 0.0, blended)
        maskedGray.copyTo(blended, mask)

        val resultRgba = Mat()
        Imgproc.cvtColor(blended, resultRgba, Imgproc.COLOR_BGR2RGBA) // 다시 RGBA로

        val resultBitmap = Bitmap.createBitmap(resultRgba.cols(), resultRgba.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultRgba, resultBitmap)

        rgbaMat.release()
        bgrMat.release()
        blurredMat.release()
        grayMat.release()
        grayBGRMat.release()
        mask.release()
        maskedGray.release()
        blended.release()
        resultRgba.release()

        return resultBitmap
    }


    private fun saveProcessedVideo(frames: List<Bitmap>, outputPath: String) {
        val width = frames[0].width
        val height = frames[0].height
        val frameRate = 30
        val frameTimeUs = 1_000_000L / frameRate

        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height).apply {
            setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
            setInteger(MediaFormat.KEY_BIT_RATE, 4_000_000)
            setInteger(MediaFormat.KEY_FRAME_RATE, frameRate)
            setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        }

        val encoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        val inputSurface = encoder.createInputSurface()
        encoder.start()

        val muxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        val bufferInfo = MediaCodec.BufferInfo()

        val paint = Paint().apply { isFilterBitmap = true }
        val canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)

        var trackIndex = -1
        var muxerStarted = false
        var presentationTimeUs = 0L

        for (frame in frames) {
            canvas.drawColor(Color.BLACK)
            canvas.drawBitmap(frame, 0f, 0f, paint)

            val surfaceCanvas = inputSurface.lockCanvas(null)
            surfaceCanvas.drawBitmap(canvasBitmap, 0f, 0f, null)
            inputSurface.unlockCanvasAndPost(surfaceCanvas)

            Thread.sleep(1000L / frameRate)

            var outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 0)
            while (outputBufferIndex >= 0) {
                val encodedData = encoder.getOutputBuffer(outputBufferIndex) ?: continue
                if (bufferInfo.size > 0) {
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)

                    if (!muxerStarted) {
                        trackIndex = muxer.addTrack(encoder.outputFormat)
                        muxer.start()
                        muxerStarted = true
                    }

                    bufferInfo.presentationTimeUs = presentationTimeUs
                    muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
                    presentationTimeUs += frameTimeUs
                }
                encoder.releaseOutputBuffer(outputBufferIndex, false)
                outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 0)
            }
        }

        encoder.signalEndOfInputStream()

        var outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 10000)
        while (outputBufferIndex >= 0) {
            val encodedData = encoder.getOutputBuffer(outputBufferIndex) ?: continue
            if (bufferInfo.size > 0 && muxerStarted) {
                encodedData.position(bufferInfo.offset)
                encodedData.limit(bufferInfo.offset + bufferInfo.size)
                bufferInfo.presentationTimeUs = presentationTimeUs
                muxer.writeSampleData(trackIndex, encodedData, bufferInfo)
                presentationTimeUs += frameTimeUs
            }
            encoder.releaseOutputBuffer(outputBufferIndex, false)
            outputBufferIndex = encoder.dequeueOutputBuffer(bufferInfo, 10000)
        }

        encoder.stop()
        encoder.release()
        muxer.stop()
        muxer.release()

        Log.d("VideoProcessor", "✅ Filtered video saved at: $outputPath")
    }
}