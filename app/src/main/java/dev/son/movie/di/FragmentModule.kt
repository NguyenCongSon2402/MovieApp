package dev.son.movie.di

import androidx.fragment.app.FragmentFactory

import dagger.Binds
import dagger.Module

@Module
interface FragmentModule {
    @Binds
    fun bindFragmentFactory(factory: VectorFragmentFactory): FragmentFactory


}