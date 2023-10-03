package com.oceantech.tracking.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*

import com.airbnb.mvrx.viewModel
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.core.TrackingBaseActivity

import com.oceantech.tracking.databinding.ActivityMainBinding
import java.util.*


class MainActivity : TrackingBaseActivity<ActivityMainBinding>() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "nimpe_channel_id"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as TrackingApplication).trackingComponent.inject(this)
        super.onCreate(savedInstanceState)

    }



    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }


}

