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

class Archieve2Fragment(
    private val lateId: String,
    private val nickname: String,
    private val content: String,
    private val isFailed: Boolean = false
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archieve2, container, false)

        // 딜레이 후 성공 프래그먼트 이동 (욕설 없는 상태로 여기까지 왔으므로 성공 확정)
        Handler(Looper.getMainLooper()).postDelayed({
            if (isFailed) {
                // 실패 프래그먼트로 이동
                val failureFragment = ArchieveFailureFragment.newInstance(lateId, nickname)
                failureFragment.show(parentFragmentManager, "ArchieveFailureFragment")
            } else {
                // 성공 프래그먼트로 이동
                val successFragment = ArchieveSuccessFragment.newInstance(lateId, nickname, content)
                successFragment.show(parentFragmentManager, "ArchieveSuccessFragment")
            }
            dismiss()
        }, 3000)


        return view
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {
        fun newInstance(
            lateId: String,
            nickname: String,
            content: String,
            isFailed: Boolean = false
        ): Archieve2Fragment {
            return Archieve2Fragment(lateId, nickname, content, isFailed)
        }
    }

}
