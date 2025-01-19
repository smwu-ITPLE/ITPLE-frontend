package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // 조문 공간 생성 버튼 클릭 이벤트
        val createButton = view.findViewById<View>(R.id.create_button)
        createButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Create1Fragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // 조문 공간 검색 버튼 클릭 이벤트
        val searchButton = view.findViewById<View>(R.id.search_button)
        searchButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SearchFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
