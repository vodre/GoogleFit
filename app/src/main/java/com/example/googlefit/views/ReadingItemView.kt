package com.example.googlefit.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.googlefit.R
import com.example.googlefit.databinding.ViewReadingItemBinding
import com.example.googlefit.extensions.isVisible
import com.example.googlefit.extensions.layoutInflater
import com.example.googlefit.model.BloodPressure

const val MAX_NORMAL_SYSTOLIC = 120
const val MIN_NORMAL_DIASTOLIC = 80

class ReadingItemView(
    context: Context,
    attributeSet: AttributeSet? = null,
) : ConstraintLayout(
    ContextThemeWrapper(context, R.style.Theme_GoogleFit),
    attributeSet,
) {
    private val binding = ViewReadingItemBinding.inflate(layoutInflater, this, true)

    fun render(reading: BloodPressure) {
        binding.systolicTextView.text = context.getString(R.string.systolic, reading.systolic)
        binding.diastolicTextView.text = context.getString(R.string.diastolic, reading.diastolic)
        binding.dateTextView.text = reading.date
        binding.timeTextView.text = reading.time
        binding.exclamation.isVisible =
            reading.systolic?.toDouble()?.toInt() ?: 0 > MAX_NORMAL_SYSTOLIC &&
            reading.diastolic?.toDouble()?.toInt() ?: 0 < MIN_NORMAL_DIASTOLIC
        binding.timeTextView.isVisible = binding.exclamation.isVisible.not()

        binding.systolicTextView.setTextColor(
            ContextCompat.getColor(
                context,
                if (reading.systolic?.toDouble()?.toInt() ?: 0 > MAX_NORMAL_SYSTOLIC)
                    R.color.red
                else
                    R.color.white
            )
        )

        binding.diastolicTextView.setTextColor(
            ContextCompat.getColor(
                context,
                if (reading.diastolic?.toDouble()?.toInt() ?: 0 < MIN_NORMAL_DIASTOLIC)
                    R.color.red
                else
                    R.color.white
            )
        )
    }
}
