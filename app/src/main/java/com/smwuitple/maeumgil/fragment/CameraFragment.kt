package com.smwuitple.maeumgil.fragment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.utils.VideoProcessor
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.smwuitple.maeumgil.R


class CameraFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var recordButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        previewView = view.findViewById(R.id.previewView)
        recordButton = view.findViewById(R.id.popup_btn)
        val closeButton: TextView = view.findViewById(R.id.btn_close)

        cameraExecutor = Executors.newSingleThreadExecutor()

        closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recordButton.setOnClickListener {
            if (recording != null) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        return view
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun startRecording() {
        val videoCapture = this.videoCapture ?: return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "video_${System.currentTimeMillis()}")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        }

        val outputOptions = MediaStoreOutputOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        recording = videoCapture.output
            .prepareRecording(requireContext(), outputOptions)
            .start(ContextCompat.getMainExecutor(requireContext())) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        recordButton.text = "녹화 중지"
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!event.hasError()) {
                            Log.d("CameraFragment", "Video saved: ${event.outputResults.outputUri}")
                            processCapturedVideo(event.outputResults.outputUri.toString())
                        } else {
                            Log.e("CameraFragment", "Recording error: ${event.error}")
                        }
                        recording = null
                        recordButton.text = "촬영하기"
                    }
                }
            }
    }

    private fun stopRecording() {
        recording?.stop()
        recording = null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Log.e("CameraFragment", "카메라 권한이 거부되었습니다.")
        }
    }

    private fun processCapturedVideo(videoPath: String) {
        val outputVideoPath = "${requireContext().filesDir}/processed_video.mp4"

        VideoProcessor.applyFilters(requireContext(), videoPath, outputVideoPath) { success ->
            if (success) {
                saveVideoToGallery(outputVideoPath) // 필터 처리 후 저장 실행
            } else {
                Log.e("CameraFragment", "Video processing failed")
            }
        }
    }


    private fun saveVideoToGallery(videoPath: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "filtered_video_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Maeumgil")
        }

        val contentResolver = requireContext().contentResolver
        val videoUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        videoUri?.let {
            val outputStream = contentResolver.openOutputStream(it)
            val inputStream = File(videoPath).inputStream()

            inputStream.copyTo(outputStream!!)
            inputStream.close()
            outputStream.close()

            Log.d("CameraFragment", "Filtered video saved to gallery: $videoUri")
        } ?: Log.e("CameraFragment", "Failed to save video to gallery")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
