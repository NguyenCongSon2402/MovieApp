package dev.son.movie.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(factory: TrackingViewModelFactory): ViewModelProvider.Factory


}