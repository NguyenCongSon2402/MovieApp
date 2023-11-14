package dev.son.movie.di

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dev.son.movie.network.service.*
import dev.son.movie.network.repository.HomeRepository

import dev.son.movie.utils.LocalHelper
import dagger.Module
import dagger.Provides
import dev.son.movie.data.local.UserPreferences
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
    fun providerUserPreferences(context: Context): UserPreferences = UserPreferences(context)
    @Provides
    fun providerHomeApi(
        remoteDataSource: RemoteDataSource
    ) = remoteDataSource.buildApi(HomeApi::class.java)

    @Provides
    fun providerSearchApi(
        remoteDataSource: RemoteDataSource
    ) = remoteDataSource.buildApi(SearchApi::class.java)

    @Provides
    @Singleton
    fun provideFireBaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
    @Provides
    @Singleton
    fun provideFireBaseStoreInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        database: FirebaseDatabase,storage: FirebaseStorage,userPreferences: UserPreferences
    ): FirebaseRepository = FirebaseRepository(database,storage,userPreferences)


    @Provides
    fun providerHomeRepository(
        api: HomeApi
    ): HomeRepository = HomeRepository(api)

    @Provides
    fun providerSearchRepository(
        api: SearchApi
    ): SearchRepository = SearchRepository(api)
}