package dev.son.movie.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import dev.son.movie.network.service.*
import dev.son.movie.network.repository.HomeRepository

import dev.son.movie.utils.LocalHelper
import dagger.Module
import dagger.Provides
import dev.son.movie.network.repository.FirebaseRepository
import dev.son.movie.network.repository.SearchRepository
import javax.inject.Singleton

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
    fun providerSearchApi(
        remoteDataSource: RemoteDataSource, context: Context
    ) = remoteDataSource.buildApi(SearchApi::class.java, context)

    @Provides
    @Singleton
    fun provideFireBaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        database: FirebaseDatabase
    ): FirebaseRepository = FirebaseRepository(database)


    @Provides
    fun providerHomeRepository(
        api: HomeApi
    ): HomeRepository = HomeRepository(api)

    @Provides
    fun providerSearchRepository(
        api: SearchApi
    ): SearchRepository = SearchRepository(api)
}