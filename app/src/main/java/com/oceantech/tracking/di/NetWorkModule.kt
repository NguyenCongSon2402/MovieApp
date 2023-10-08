package com.oceantech.tracking.di

import android.content.Context
import com.oceantech.tracking.data.network.*
import com.oceantech.tracking.data.repository.HomeRepository

import com.oceantech.tracking.utils.LocalHelper
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