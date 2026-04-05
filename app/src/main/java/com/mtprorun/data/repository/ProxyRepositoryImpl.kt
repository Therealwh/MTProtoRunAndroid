package com.mtprorun.data.repository

import com.mtprorun.data.local.DataStoreManager
import com.mtprorun.data.model.ProxyDto
import com.mtprorun.data.model.ProxyPingResult
import com.mtprorun.data.remote.ProxyApiService
import com.mtprorun.data.util.CountryResolver
import com.mtprorun.data.util.PingChecker
import com.mtprorun.domain.model.FilterState
import com.mtprorun.domain.model.ProxyUi
import com.mtprorun.domain.model.SortOption
import com.mtprorun.domain.repository.ProxyRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProxyRepositoryImpl @Inject constructor(
    private val api: ProxyApiService,
    private val pingChecker: PingChecker,
    private val countryResolver: CountryResolver,
    private val dataStoreManager: DataStoreManager,
    private val gson: Gson
) : ProxyRepository {

    private val _proxies = MutableStateFlow<List<ProxyUi>>(emptyList())

    override fun getProxiesStream(): Flow<List<ProxyUi>> = _proxies.asStateFlow()

    override suspend fun refreshProxies(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val dtos = api.getProxies()
            val proxies = dtos.mapIndexed { _, dto ->
                val countryCode = countryResolver.resolveCountry(dto.host, dto.region)
                ProxyUi(
                    id = UUID.randomUUID().toString(),
                    host = dto.host,
                    port = dto.port,
                    secret = dto.secret,
                    tgLink = dto.link ?: buildTgLink(dto.host, dto.port, dto.secret),
                    pingMs = dto.ping?.let { (it * 1000).toInt() },
                    countryCode = countryCode,
                    isOnline = false,
                    lastChecked = 0L,
                    regionHint = dto.region,
                    method = dto.method
                )
            }
            _proxies.value = proxies
            dataStoreManager.setProxyCache(gson.toJson(proxies))
            dataStoreManager.setLastUpdate(System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            loadFromCache()
            if (_proxies.value.isEmpty()) {
                Result.failure(e)
            } else {
                Result.success(Unit)
            }
        }
    }

    private suspend fun loadFromCache() {
        try {
            val cached = dataStoreManager.proxyCache.first()
            if (cached.isNotBlank()) {
                val type = object : TypeToken<List<ProxyUi>>() {}.type
                val cachedProxies: List<ProxyUi> = gson.fromJson(cached, type)
                _proxies.value = cachedProxies
            }
        } catch (e: Exception) {
            // Ignore cache errors
        }
    }

    override suspend fun checkProxyPing(proxyId: String): Result<ProxyPingResult> =
        withContext(Dispatchers.IO) {
            val proxy = _proxies.value.find { it.id == proxyId }
                ?: return@withContext Result.failure(IllegalArgumentException("Proxy not found"))

            val result = pingChecker.checkProxy(proxy.host, proxy.port)
            val updated = _proxies.value.map { p ->
                if (p.id == proxyId) {
                    p.copy(
                        pingMs = result.pingMs,
                        isOnline = result.isSuccess,
                        lastChecked = System.currentTimeMillis()
                    )
                } else p
            }
            _proxies.value = updated
            Result.success(result)
        }

    override fun applyFilters(filters: FilterState): List<ProxyUi> {
        var result = _proxies.value

        if (filters.searchQuery.isNotBlank()) {
            val query = filters.searchQuery.lowercase()
            result = result.filter { p ->
                p.host.lowercase().contains(query) ||
                    p.countryCode.lowercase().contains(query) ||
                    p.port.toString().contains(query)
            }
        }

        if (filters.selectedCountries.isNotEmpty()) {
            result = result.filter { p -> filters.selectedCountries.contains(p.countryCode) }
        }

        if (filters.onlyOnline) {
            result = result.filter { p -> p.isOnline }
        }

        if (filters.maxPingMs != null) {
            result = result.filter { p -> p.pingMs != null && p.pingMs <= filters.maxPingMs }
        }

        result = when (filters.sortBy) {
            SortOption.PING_ASC -> result.sortedWith(
                compareBy<ProxyUi> { it.pingMs ?: Int.MAX_VALUE }.thenBy { it.host }
            )
            SortOption.PING_DESC -> result.sortedWith(
                compareByDescending<ProxyUi> { it.pingMs ?: 0 }.thenBy { it.host }
            )
            SortOption.COUNTRY -> result.sortedWith(
                compareBy { it.countryCode }.thenBy { it.host }
            )
            SortOption.ADDED -> result
        }

        return result
    }

    override fun getProxyById(id: String): ProxyUi? = _proxies.value.find { it.id == id }

    override fun getAvailableCountries(): List<String> =
        _proxies.value.map { it.countryCode }.distinct().sorted()

    private fun buildTgLink(host: String, port: Int, secret: String): String =
        "tg://proxy?server=$host&port=$port&secret=$secret"
}
