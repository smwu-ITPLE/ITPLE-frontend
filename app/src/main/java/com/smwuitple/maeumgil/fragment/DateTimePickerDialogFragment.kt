package com.smwuitple.maeumgil.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DateTimePickerFragment(
    private val dateTimeInput : EditText,
    private val onDateTimeSelected: (String) -> Unit)
    : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(requireContext(), { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)

            TimePickerDialog(requireContext(), { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                val uiFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분", Locale.getDefault())
                val uiDateTime = uiFormat.format(calendar.time)

                val backendFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:00", Locale.getDefault())
                val backendDateTime = backendFormat.format(calendar.time)

                dateTimeInput.setText(uiDateTime)
                onDateTimeSelected(backendDateTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        return datePickerDialog
    }
}
