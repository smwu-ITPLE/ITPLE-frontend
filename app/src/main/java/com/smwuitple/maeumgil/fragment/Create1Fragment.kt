package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class Create1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create1, container, false)

        // 다음으로 버튼 클릭 이벤트
        view.findViewById<Button>(R.id.next_button).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Create2Fragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    companion object {
        fun newInstance(): Create1Fragment {
            return Create1Fragment()
        }
    }
}
