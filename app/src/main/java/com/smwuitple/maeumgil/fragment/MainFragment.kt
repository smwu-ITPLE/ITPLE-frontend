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

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Arguments 초기화
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // 버튼 클릭 이벤트 설정
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

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * MainFragment의 인스턴스를 생성하는 메서드.
         * @param param1 첫 번째 매개변수.
         * @param param2 두 번째 매개변수.
         * @return MainFragment 인스턴스.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String): MainFragment {
            return MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        }
    }
}
