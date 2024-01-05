package dev.son.movie.di

import android.content.Context
import dev.son.movie.TrackingApplication
import dev.son.movie.ui.BottomNavActivity
import dev.son.movie.ui.MovieDetailsActivity
import dev.son.movie.ui.home.HomeFragment

import dagger.BindsInstance
import dagger.Component
import dev.son.movie.ui.AccountActivity
import dev.son.movie.ui.AddMovieActivity
import dev.son.movie.ui.AdminActivity
import dev.son.movie.ui.CategoryMoviesActivity
import dev.son.movie.ui.PlayMovieOfflineActivity
import dev.son.movie.ui.SignUpActivity
import dev.son.movie.ui.SplashActivity
import dev.son.movie.ui.login.LoginActivity
import dev.son.movie.ui.search.SearchActivity
import dev.son.movie.ui.more.MoreFragment
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
    fun inject(splashActivity: SplashActivity)
    fun inject(signUpActivity: SignUpActivity)
    fun inject(activity: AccountActivity)
    fun inject(searchActivity: SearchActivity)
    fun inject(categoryMoviesActivity: CategoryMoviesActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(moreFragment: MoreFragment)

    fun inject(movieDetailsActivity: MovieDetailsActivity)
    fun inject(addMovieActivity: AddMovieActivity)
    fun inject(playMovieOfflineActivity: PlayMovieOfflineActivity)
    fun inject(adminActivity: AdminActivity)

    //fun fragmentFactory(): FragmentFactory
    //fun viewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): TrackingComponent
    }
}