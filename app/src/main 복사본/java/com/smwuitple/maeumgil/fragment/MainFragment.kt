package com.smwuitple.maeumgil.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.LoginActivity
import com.smwuitple.maeumgil.R
import com.smwuitple.maeumgil.SignupActivity

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val signupButton = view.findViewById<Button>(R.id.btn_signup)

        loginButton.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener {
            val intent = Intent(requireContext(), SignupActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}
