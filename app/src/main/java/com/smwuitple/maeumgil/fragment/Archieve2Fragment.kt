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
import com.smwuitple.maeumgil.utils.CurseWordDetector

class Archieve2Fragment(
    private val lateId: String,
    private val nickname: String,
    private val content: String
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve2, container, false)

        // 욕설 데이터 로드
        CurseWordDetector.loadCurseWords(requireContext())

        Handler(Looper.getMainLooper()).postDelayed({
            val (_, detected) = CurseWordDetector.filterText(content)

            if (detected) {
                val failureFragment = ArchieveFailureFragment.newInstance(lateId, nickname, content)
                failureFragment.show(parentFragmentManager, "ArchieveFailureFragment")
            } else {
                val successFragment = ArchieveSuccessFragment.newInstance(lateId, nickname, content)
                successFragment.show(parentFragmentManager, "ArchieveSuccessFragment")
            }
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
