package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class SignupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        // 회원가입 버튼 클릭 이벤트 처리
        val signupButton = view.findViewById<Button>(R.id.btn_signup)
        signupButton.setOnClickListener {
            Toast.makeText(context, "회원가입 성공", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        /**
         * SignupFragment의 인스턴스를 생성하는 메서드.
         * @return SignupFragment 인스턴스.
         */
        fun newInstance(): SignupFragment {
            return SignupFragment()
        }
    }
}
