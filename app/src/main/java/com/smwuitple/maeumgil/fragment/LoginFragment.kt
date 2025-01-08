package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class LoginFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // 로그인 버튼 클릭 이벤트
        val loginButton = view.findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * LoginFragment의 인스턴스를 생성하는 메서드.
         * @param param1 첫 번째 매개변수.
         * @param param2 두 번째 매개변수.
         * @return LoginFragment 인스턴스.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String): LoginFragment {
            return LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        }
    }
}
