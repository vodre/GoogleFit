package com.example.googlefit.model

internal data class UiModel(
    val uiStatus: UiStatus,
    val readings: List<BloodPressure>? = null,
    val lastSync: String? = null
) {
    companion object {
        val default
            get() = UiModel(
                uiStatus = Idle,
            )
    }
}

data class BloodPressure(
    var date: String? = null,
    var time: String? = null,
    var systolic: String? = null,
    var diastolic: String? = null,
)
