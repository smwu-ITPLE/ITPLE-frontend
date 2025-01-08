package com.smwuitple.maeumgil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smwuitple.maeumgil.fragment.LoginFragment

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // 올바른 레이아웃 확인

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment()) // LoginFragment 로드
                .commit()
        }
    }
}
