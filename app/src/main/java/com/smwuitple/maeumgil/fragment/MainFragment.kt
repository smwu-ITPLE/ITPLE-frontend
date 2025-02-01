package com.smwuitple.maeumgil.fragment

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

        loginButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment.newInstance())
                .commit()
        }

        signupButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignupFragment.newInstance())
                .commit()
        }

        return view
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}
