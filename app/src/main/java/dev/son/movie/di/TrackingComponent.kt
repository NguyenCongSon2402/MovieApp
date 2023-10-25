package dev.son.movie.di

import android.content.Context
import dev.son.movie.TrackingApplication
import dev.son.movie.ui.BottomNavActivity
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.TvDetailsActivity
import dev.son.movie.ui.home.HomeFragment

import dagger.BindsInstance
import dagger.Component
import dev.son.movie.ui.SignUpActivity
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.ui.search.SearchActivity
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
    fun inject(loginActivity: LoginActivity)
    fun inject(signUpActivity: SignUpActivity)
    fun inject(searchActivity: SearchActivity)
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