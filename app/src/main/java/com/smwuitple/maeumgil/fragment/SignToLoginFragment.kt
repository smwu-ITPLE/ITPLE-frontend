package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class SignToLoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_to_login, container, false)

        val loginButton = view.findViewById<Button>(R.id.popup_btn)
        loginButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    companion object {
        fun newInstance(): SignToLoginFragment {
            return SignToLoginFragment()
        }
    }
}
