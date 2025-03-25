package com.smwuitple.maeumgil

import android.app.Application
import android.util.Log
import org.opencv.android.OpenCVLoader
import com.smwuitple.maeumgil.utils.CurseWordDetector


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        CurseWordDetector.loadCurseWords(this)
        
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initialization failed!")
        } else {
            Log.d("OpenCV", "OpenCV successfully initialized")
        }
    }
}
