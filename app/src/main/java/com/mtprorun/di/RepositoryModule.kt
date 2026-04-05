package com.mtprorun.di

import com.mtprorun.data.local.DataStoreManager
import com.mtprorun.data.remote.ProxyApiService
import com.mtprorun.data.repository.ProxyRepositoryImpl
import com.mtprorun.data.util.CountryResolver
import com.mtprorun.data.util.PingChecker
import com.mtprorun.domain.repository.ProxyRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProxyRepository(
        api: ProxyApiService,
        pingChecker: PingChecker,
        countryResolver: CountryResolver,
        dataStoreManager: DataStoreManager,
        gson: Gson
    ): ProxyRepository =
        ProxyRepositoryImpl(api, pingChecker, countryResolver, dataStoreManager, gson)
}
