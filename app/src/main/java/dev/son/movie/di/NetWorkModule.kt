package dev.son.movie.di

import android.content.Context
import dev.son.movie.data.network.*
import dev.son.movie.data.repository.HomeRepository

import dev.son.movie.utils.LocalHelper
import dagger.Module
import dagger.Provides

@Module
object NetWorkModule {
    @Provides
    fun providerLocaleHelper(): LocalHelper = LocalHelper()

    @Provides
    fun providerRemoteDateSource(): RemoteDataSource = RemoteDataSource()

    @Provides
    fun providerHomeApi(
        remoteDataSource: RemoteDataSource, context: Context
    ) = remoteDataSource.buildApi(HomeApi::class.java, context)

    @Provides
    fun providerHomeRepository(
        api: HomeApi
    ): HomeRepository = HomeRepository(api)
}