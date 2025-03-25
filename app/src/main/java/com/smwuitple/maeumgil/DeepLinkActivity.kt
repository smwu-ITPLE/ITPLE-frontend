package com.smwuitple.maeumgil

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.smwuitple.maeumgil.fragment.PrivateMainFragment

class DeepLinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data != null) {
            val lateId = data.lastPathSegment // lateId 추출

            if (lateId != null) {
                val fragment = PrivateMainFragment.newInstance(lateId.toString())
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            }
        }

        finish()
    }
}
