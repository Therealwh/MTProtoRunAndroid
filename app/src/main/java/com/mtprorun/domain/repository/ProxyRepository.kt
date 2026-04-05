package com.mtprorun.domain.repository

import com.mtprorun.data.model.ProxyPingResult
import com.mtprorun.domain.model.FilterState
import com.mtprorun.domain.model.ProxyUi
import kotlinx.coroutines.flow.Flow

interface ProxyRepository {
    fun getProxiesStream(): Flow<List<ProxyUi>>
    suspend fun refreshProxies(): Result<Unit>
    suspend fun checkProxyPing(proxyId: String): Result<ProxyPingResult>
    fun applyFilters(filters: FilterState): List<ProxyUi>
    fun getProxyById(id: String): ProxyUi?
    fun getAvailableCountries(): List<String>
}
