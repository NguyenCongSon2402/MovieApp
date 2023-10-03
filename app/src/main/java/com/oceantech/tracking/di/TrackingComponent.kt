package com.oceantech.tracking.di

import android.content.Context
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.ui.MainActivity

import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    NetWorkModule::class
])
@Singleton
interface TrackingComponent {
    fun inject(trackingApplication: TrackingApplication)
    fun inject(mainActivity: MainActivity)

    //fun fragmentFactory(): FragmentFactory
    //fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TrackingComponent
    }
}