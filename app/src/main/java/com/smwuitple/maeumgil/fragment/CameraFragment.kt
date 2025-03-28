package com.smwuitple.maeumgil.fragment

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var recordButton: Button
    private lateinit var switchCameraButton: ImageButton
    private lateinit var loadingContainer: View

    private var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        previewView = view.findViewById(R.id.previewView)
        recordButton = view.findViewById(R.id.popup_btn)
        switchCameraButton = view.findViewById(R.id.btn_switch_camera)
        loadingContainer = view.findViewById(R.id.loading_container)
        val closeButton: TextView = view.findViewById(R.id.btn_close)

        cameraExecutor = Executors.newSingleThreadExecutor()

        closeButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        recordButton.setOnClickListener {
            if (recording != null) stopRecording() else startRecording()
        }

        switchCameraButton.setOnClickListener {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
            startCamera()
        }

        checkStoragePermission()

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

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

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
        val videoFile = File(requireContext().filesDir, "video_${System.currentTimeMillis()}.mp4")
        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        recording = videoCapture.output
            .prepareRecording(requireContext(), outputOptions)
            .start(ContextCompat.getMainExecutor(requireContext())) { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        recordButton.text = "녹화 중지"
                    }
                    is VideoRecordEvent.Finalize -> {
                        recordButton.text = "촬영하기"
                        recording = null
                        if (!event.hasError()) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                processCapturedVideo(videoFile.absolutePath)
                            }, 300)
                        } else {
                            Toast.makeText(requireContext(), "녹화 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    private fun stopRecording() {
        recordButton.text = "처리 중..."
        recording?.stop()
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1001
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) startCamera()
        else Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
    }

    private fun processCapturedVideo(videoPath: String) {
        loadingContainer.visibility = View.VISIBLE

        val file = File(videoPath)
        val requestFile = file.asRequestBody("video/mp4".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("video", file.name, requestFile)

        ApiClient.instance.uploadVideo(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                loadingContainer.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val processedVideo = response.body()!!
                    val outFile = File(requireContext().filesDir, "processed_video.mp4")
                    outFile.outputStream().use { output ->
                        processedVideo.byteStream().copyTo(output)
                    }
                    saveVideoToGallery(outFile.absolutePath)
                    Toast.makeText(requireContext(), "영상 저장 완료", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "서버 처리 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                loadingContainer.visibility = View.GONE
                Toast.makeText(requireContext(), "서버 요청 실패: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("CameraFragment", "❌ Error: ${t.message}")
            }
        })
    }

    private fun saveVideoToGallery(videoPath: String) {
        val resolver = requireContext().contentResolver
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val newVideoDetails = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "filtered_video_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Maeumgil")
            }
        }

        val videoUri = resolver.insert(videoCollection, newVideoDetails)

        if (videoUri != null) {
            resolver.openOutputStream(videoUri).use { outputStream ->
                File(videoPath).inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }
            Log.d("CameraFragment", "✅ 저장 완료: $videoUri")
        } else {
            Log.e("CameraFragment", "❌ 저장 실패")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
