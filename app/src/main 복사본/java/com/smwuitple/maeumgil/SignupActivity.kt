package com.smwuitple.maeumgil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smwuitple.maeumgil.fragment.SignupFragment

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup) // 올바른 액티비티 레이아웃 사용

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction
                .replace(R.id.fragment_container, SignupFragment()) // SignupFragment 로드
                .commit()
        }
    }
}
