package com.mtprorun.di

import android.content.Context
import com.google.gson.Gson
import com.mtprorun.data.local.DataStoreManager
import com.mtprorun.data.util.CountryResolver
import com.mtprorun.data.util.DnsResolver
import com.mtprorun.data.util.PingChecker
import com.mtprorun.domain.repository.ProxyRepository
import com.mtprorun.domain.usecase.CheckProxyPingUseCase
import com.mtprorun.domain.usecase.FilterProxiesUseCase
import com.mtprorun.domain.usecase.GetProxiesUseCase
import com.mtprorun.domain.usecase.RefreshProxiesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun providePingChecker(): PingChecker = PingChecker()

    @Provides
    @Singleton
    fun provideDnsResolver(): DnsResolver = DnsResolver()

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

    @Provides
    @Singleton
    fun provideGetProxiesUseCase(repository: ProxyRepository): GetProxiesUseCase =
        GetProxiesUseCase(repository)

    @Provides
    @Singleton
    fun provideCheckProxyPingUseCase(repository: ProxyRepository): CheckProxyPingUseCase =
        CheckProxyPingUseCase(repository)

    @Provides
    @Singleton
    fun provideFilterProxiesUseCase(repository: ProxyRepository): FilterProxiesUseCase =
        FilterProxiesUseCase(repository)

    @Provides
    @Singleton
    fun provideRefreshProxiesUseCase(repository: ProxyRepository): RefreshProxiesUseCase =
        RefreshProxiesUseCase(repository)
}
