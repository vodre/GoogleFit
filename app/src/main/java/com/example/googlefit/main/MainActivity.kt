package com.example.googlefit.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.googlefit.R
import com.example.googlefit.databinding.ActivityMainBinding
import com.example.googlefit.model.BloodPressure
import com.example.googlefit.model.Failure
import com.example.googlefit.model.Idle
import com.example.googlefit.model.Loading
import com.example.googlefit.model.Success
import com.example.googlefit.model.UiModel
import com.example.googlefit.utils.SimpleDividerItemDecoration
import com.example.googlefit.views.ReadingItemView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.cycler.Recycler
import com.squareup.cycler.toDataSource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

private const val REQUEST_OAUTH_REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var recycler: Recycler<BloodPressure>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureRecycler()
        binding.enableBtn.setOnClickListener {
            requestGoogleFitPermissions()
        }

        viewModel.viewStateFlow
            .flowWithLifecycle(lifecycle)
            .onEach { render(it) }
            .launchIn(lifecycleScope)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                REQUEST_OAUTH_REQUEST_CODE -> enableGoogleFit()
                else -> {
                    Timber.d("Result wasn't from Google Fit")
                }
            }
            else -> {
                Timber.e("Permission not granted")
            }
        }
    }

    private fun render(uiModel: UiModel) {
        with(uiModel) {
            when (uiStatus) {
                Idle -> Timber.d("Idle")
                Loading -> Timber.d("Loading...")
                Success -> {
                    uiModel.readings?.toDataSource()?.let {
                        recycler.update {
                            data = it
                        }
                    }

                    binding.googleSyncFit.text =
                        getString(R.string.last_google_sync_fit_date, uiModel.lastSync)
                }
                is Failure -> Timber.e(uiStatus.error)
                else -> Unit // no action
            }
        }
    }

    private fun configureRecycler() {
        recycler = Recycler.adopt(binding.recycler) {
            row<BloodPressure, ReadingItemView> {
                create { context ->
                    view = ReadingItemView(context)
                    bind { reading ->
                        view.render(reading)
                    }
                }
            }
        }

        recycler.view
            .addItemDecoration(SimpleDividerItemDecoration(this, R.drawable.line_divider))
    }

    private fun requestGoogleFitPermissions() {
        if (!GoogleSignIn.hasPermissions(viewModel.account, viewModel.fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                REQUEST_OAUTH_REQUEST_CODE,
                viewModel.account,
                viewModel.fitnessOptions
            )
        } else {
            enableGoogleFit()
        }
    }

    private fun enableGoogleFit() = viewModel.enableGoogleFit()
}
