package dev.son.movie.di

import android.content.Context
import dev.son.movie.TrackingApplication
import dev.son.movie.ui.BottomNavActivity
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.TvDetailsActivity
import dev.son.movie.ui.home.HomeFragment

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
    fun inject(tvDetailsActivity: TvDetailsActivity)

    //fun fragmentFactory(): FragmentFactory
    //fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TrackingComponent
    }
}