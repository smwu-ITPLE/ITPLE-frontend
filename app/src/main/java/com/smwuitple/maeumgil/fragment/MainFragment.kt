package com.smwuitple.maeumgil.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R


class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val signupButton = view.findViewById<Button>(R.id.btn_signup)

        // 로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener {
            // 이동 방법 선택 (LoginActivity 또는 LoginFragment)
            // Uncomment one of the following options:

            // Option 1: Fragment 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment.newInstance())
                .addToBackStack(null)
                .commit()

            // Option 2: Activity 전환
            /*
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            */
        }

        // 회원가입 버튼 클릭 이벤트
        signupButton.setOnClickListener {
            // 이동 방법 선택 (SignupActivity 또는 SignupFragment)
            // Uncomment one of the following options:

            // Option 1: Fragment 전환
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignupFragment.newInstance())
                .addToBackStack(null)
                .commit()

            // Option 2: Activity 전환
            /*
            val intent = Intent(requireContext(), SignupActivity::class.java)
            startActivity(intent)
            */
        }

        return view
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}
