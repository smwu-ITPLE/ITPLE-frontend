package com.smwuitple.maeumgil.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class Create2Fragment : Fragment() {

    private lateinit var dateDeathInput : EditText
    private lateinit var locationInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var nextButton: Button
    private lateinit var backButton: ImageView

    private var selectedDateTimeForBackend: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateDeathInput = view.findViewById(R.id.date_time_input)
        locationInput = view.findViewById(R.id.location_textview_input_edit_text)
        contentInput = view.findViewById(R.id.context_textview_input_edit_text)
        nextButton = view.findViewById(R.id.next_button)
        backButton = view.findViewById(R.id.back_button)

        dateDeathInput.setOnClickListener {
            val dateTimePicker = DateTimePickerFragment(dateDeathInput) { selectedDateTime ->
                selectedDateTimeForBackend = selectedDateTime // 백엔드 전송용 데이터 저장
            }
            dateTimePicker.show(parentFragmentManager, "dateTimePicker")
        }

        val name = arguments?.getString("name")
        val age = arguments?.getInt("age")
        val gender = arguments?.getString("gender")
        val datePass = arguments?.getString("datePass")
        val profile = arguments?.getString("profile")

        nextButton.setOnClickListener {
            moveToNextFragment(name, age, gender, datePass, profile)
        }

        backButton.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager

            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun moveToNextFragment(name: String?, age: Int?, gender: String?, datePass: String?, profile: String?) {
        val location = locationInput.text.toString().trim()
        val content = contentInput.text.toString().trim()
        val datePassForBackend = selectedDateTimeForBackend

        // 입력값 검증
        if (datePassForBackend.isNullOrEmpty() || location.isEmpty() ) {
            Toast.makeText(requireContext(), "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        val bundle = Bundle().apply {
            putString("name", name)
            putInt("age", age ?: 0)
            putString("gender", gender)
            putString("datePass", datePass)
            putString("profile", profile)
            putString("dateDeath", datePassForBackend)
            putString("location", location)
            putString("content", content)
        }

        val fragment = Create3Fragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
