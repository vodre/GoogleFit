package com.example.googlefit.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.googlefit.extensions.formatToViewDateDefault
import com.example.googlefit.extensions.formatToViewTimeDefault
import com.example.googlefit.model.BloodPressure
import com.example.googlefit.model.Success
import com.example.googlefit.model.UiModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "vdrx"

    private val _viewStateFlow = MutableStateFlow(UiModel.default)
    internal val viewStateFlow get() = _viewStateFlow.asStateFlow()

    @SuppressLint("StaticFieldLeak")
    private val context: Context = application.applicationContext

    val fitnessOptions = FitnessOptions.builder()
        .accessActivitySessions(FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
        .build()

    val account = GoogleSignIn.getAccountForExtension(
        context,
        fitnessOptions
    )

    fun enableGoogleFit() {
        val end = LocalDateTime.now()
        val start = end.minusMonths(1)
        val endSeconds = end.atZone(ZoneId.systemDefault()).toEpochSecond()
        val startSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond()
        val readings = mutableListOf<BloodPressure>()

        val readRequest = DataReadRequest.Builder()
            .aggregate(
                HealthDataTypes.TYPE_BLOOD_PRESSURE,
                HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY
            )
            .setTimeRange(startSeconds, endSeconds, TimeUnit.SECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .enableServerQueries()
            .build()
        Fitness.getHistoryClient(context, account)
            .readData(readRequest)
            .addOnSuccessListener { response ->
                var lastSync: String? = null
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    for (dp in dataSet.dataPoints) {
                        val reading = BloodPressure()
                        if (lastSync.isNullOrEmpty()) {
                            lastSync =
                                Date(dp.getEndTime(TimeUnit.MILLISECONDS)).formatToViewDateDefault()
                        }
                        reading.date =
                            Date(dp.getStartTime(TimeUnit.MILLISECONDS)).formatToViewDateDefault()
                        reading.time =
                            Date(dp.getStartTime(TimeUnit.MILLISECONDS)).formatToViewTimeDefault()
                        for (field in dp.dataType.fields) {
                            when (field.name) {
                                "blood_pressure_systolic_average" -> {
                                    reading.systolic = dp.getValue(field).toString()
                                }
                                "blood_pressure_diastolic_average" -> {
                                    reading.diastolic = dp.getValue(field).toString()
                                }
                            }
                        }
                        readings.add(reading)
                    }
                }
                _viewStateFlow.value = _viewStateFlow.value.copy(
                    uiStatus = Success,
                    readings = readings,
                    lastSync = lastSync,
                )
            }
            .addOnFailureListener { e ->
                Timber.e(e)
            }
    }
}
