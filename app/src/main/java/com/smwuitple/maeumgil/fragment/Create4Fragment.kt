package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class Create4Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create4, container, false)

        // 홈 버튼 클릭 이벤트
        view.findViewById<Button>(R.id.home_button).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }

        // 관리 버튼 클릭 이벤트->임의로 homefragemnt 로
        view.findViewById<Button>(R.id.manage_button).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }

        // 공유하기 버튼 (옵션: 구현된 기능 추가 가능)
        view.findViewById<Button>(R.id.share_button).setOnClickListener {
            // 공유 기능 구현
        }

        return view
    }

    companion object {
        fun newInstance(): Create4Fragment {
            return Create4Fragment()
        }
    }
}
