package com.example.googlefit.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.googlefit.MainActivity
import com.example.googlefit.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.android.gms.fitness.service.FitnessSensorService
import com.google.android.gms.fitness.service.FitnessSensorServiceRequest
import timber.log.Timber
import java.util.concurrent.TimeUnit

const val CHANNEL_ID = "NOTIFICATION_CHANNEL"

class SensorService : FitnessSensorService() {

    private val fitnessOptions = FitnessOptions.builder()
        .accessActivitySessions(FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY, FitnessOptions.ACCESS_READ)
        .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
        .build()

    private val account = GoogleSignIn.getAccountForExtension(
        this,
        fitnessOptions
    )

    private lateinit var mListener: OnDataPointListener

    override fun onCreate() {
        super.onCreate()

        mListener = OnDataPointListener { _ ->
            showNotification("Fit Activity", "Google Fit Activity Detected")
        }

        Fitness.getSensorsClient(this, account)
            .findDataSources(
                DataSourcesRequest.Builder()
                    .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                    .setDataSourceTypes(DataSource.TYPE_RAW)
                    .build()
            )
            .addOnSuccessListener { dataSources ->
                for (dataSource in dataSources) {
                    Timber.i("Data source found: $dataSource")
                    Timber.i("Data Source type: " + dataSource.dataType.name)
                }
            }
            .addOnFailureListener { e -> Timber.e(e) }
    }

    override fun onFindDataSources(p0: MutableList<DataType>): MutableList<DataSource> {
        TODO("Not yet implemented")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(title: String, message: String) {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Sensor Service ",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Google Fit Activity"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

    override fun onRegister(request: FitnessSensorServiceRequest): Boolean {
        Fitness.getSensorsClient(this, account)
            .add(
                SensorRequest.Builder()
                    .setDataType(DataType.TYPE_HEART_RATE_BPM)
                    .setSamplingRate(10, TimeUnit.SECONDS)
                    .build(),
                mListener
            )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.i("Listener registered!")
                }
            }
        return true
    }

    override fun onUnregister(dataSource: DataSource): Boolean {
        Fitness.getSensorsClient(this, account)
            .remove(mListener)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result) {
                    Timber.i("Listener was removed!")
                } else {
                    Timber.i("Listener was not removed.")
                }
            }

        return true
    }
}
