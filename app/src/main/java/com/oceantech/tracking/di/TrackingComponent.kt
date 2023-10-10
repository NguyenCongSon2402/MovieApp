package com.oceantech.tracking.di

import android.content.Context
import com.oceantech.tracking.TrackingApplication
import com.oceantech.tracking.ui.BottomNavActivity
import com.oceantech.tracking.ui.MovieDetailsActivity
import com.oceantech.tracking.ui.home.HomeFragment

import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(
    modules = [
        NetWorkModule::class
    ]
)
@Singleton
interface TrackingComponent {
    fun inject(trackingApplication: TrackingApplication)
    fun inject(bottomNavActivity: BottomNavActivity)
    fun inject(homeFragment: HomeFragment)

    fun inject(movieDetailsActivity: MovieDetailsActivity)

    //fun fragmentFactory(): FragmentFactory
    //fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TrackingComponent
    }
}