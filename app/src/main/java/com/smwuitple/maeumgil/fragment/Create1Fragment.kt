package com.smwuitple.maeumgil.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.smwuitple.maeumgil.R

class Create1Fragment : Fragment() {

    private lateinit var nameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var radioMale: RadioButton
    private lateinit var radioFemale: RadioButton
    private lateinit var datePassInput: EditText
    private lateinit var uploadButton: Button
    private lateinit var memorialImageView: ImageView
    private lateinit var nextButton: Button
    private lateinit var backButton : ImageView

    private var selectedDateTimeForBackend: String? = null
    private var selectedImageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameInput = view.findViewById(R.id.name_textview_input_edit_text)
        ageInput = view.findViewById(R.id.age_input)
        radioGroupGender = view.findViewById(R.id.radio_group_gender)
        radioMale = view.findViewById(R.id.radio_male)
        radioFemale = view.findViewById(R.id.radio_female)
        datePassInput = view.findViewById(R.id.date_time_input)
        uploadButton = view.findViewById(R.id.upload_memorial_image_button)
        memorialImageView = view.findViewById(R.id.memorial_image_view)
        nextButton = view.findViewById(R.id.next_button)
        backButton = view.findViewById(R.id.back_button)

        datePassInput.setOnClickListener {
            val dateTimePicker = DateTimePickerFragment(datePassInput) { selectedDateTime ->
                selectedDateTimeForBackend = selectedDateTime // 백엔드 전송용 데이터 저장
            }
            dateTimePicker.show(parentFragmentManager, "dateTimePicker")
        }

        uploadButton.setOnClickListener {
            openGallery()
        }

        nextButton.setOnClickListener {
            moveToNextFragment()
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

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            memorialImageView.setImageURI(selectedImageUri)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun moveToNextFragment() {
        val name = nameInput.text.toString().trim()
        val age = ageInput.text.toString().trim()
        val gender: String? = when {
            radioMale.isChecked -> "남"
            radioFemale.isChecked -> "여"
            else -> null
        }
        val datePassForBackend = selectedDateTimeForBackend

        // 입력값 검증
        if (name.isEmpty() || age.isEmpty() || gender == null || datePassForBackend.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        val profileUri = selectedImageUri?.toString() ?: "android.resource://${requireContext().packageName}/${R.drawable.ic_launcher_foreground}"

        val bundle = Bundle().apply {
            putString("name", name)
            putInt("age", age.toInt())
            putString("gender", gender)
            putString("datePass", datePassForBackend)
            putString("profile", profileUri)
        }

        val fragment = Create2Fragment()
        fragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance(): Create1Fragment {
            return Create1Fragment()
        }
    }
}
