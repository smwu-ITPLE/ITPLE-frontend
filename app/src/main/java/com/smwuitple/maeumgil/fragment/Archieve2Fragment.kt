package com.smwuitple.maeumgil.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.smwuitple.maeumgil.R

class Archieve2Fragment(private val lateId: String,
                        private val nickname: String,
                        private val content: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve2, container, false)

        // TODO: 윤녕님 여기 코드 적어주세요 ~~ !! 일단은 2~3초 대기로 해놨습니다
        //  content에서 텍스트 분석해서,
        //  성공이면 ArchieveSuccessFragment로 이동 / 실패면 ArchieveFailureFragment로 이동해주세요

        // 2~3초 후 API 호출 후 성공 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            val successFragment = ArchieveSuccessFragment.newInstance(lateId, nickname, content)
            successFragment.show(parentFragmentManager, "ArchieveSuccessFragment")
            dismiss()
        }, 3000) // 3초 대기

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        fun newInstance(lateId: String, nickname: String, content: String): Archieve2Fragment {
            return Archieve2Fragment(lateId, nickname, content)
        }
    }
}
