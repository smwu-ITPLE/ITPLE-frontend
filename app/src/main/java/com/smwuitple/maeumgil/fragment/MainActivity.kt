package com.smwuitple.maeumgil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit()
        }
    }
}
