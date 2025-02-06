package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smwuitple.maeumgil.R

class PrivateMainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_private_main, container, false)
        return view
    }

    companion object {
        fun newInstance(): PrivateMainFragment {
            return PrivateMainFragment()
        }
    }
}